package com.sidgowda.pawcalc.doglist

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.dateToNewFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.data.dogs.model.DogState
import com.sidgowda.pawcalc.data.dogs.model.formattedToTwoDecimals
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class FakeDogsRepo(
    private val dogsDataSource: DogsDataSource,
    isLoading: Boolean = true
) : DogsRepo {

    private val loadState = MutableStateFlow(isLoading)
    private val errorState = MutableStateFlow(false)

    override fun dogState(): Flow<DogState> {
        return combine(loadState, dogsDataSource.dogs(), errorState) { loadState, dogs, error ->
            if (error) {
                throw NoSuchElementException()
            }
            DogState(
                isLoading = loadState,
                dogs = dogs
            )
        }
    }

    override suspend fun fetchDogs() {
        loadState.update { false }
    }

    fun forceError() {
        errorState.update { true }
    }

    override suspend fun addDog(dogInput: DogInput) {
        val currentList = dogsDataSource.dogs().first()
        val id = if (currentList.isNotEmpty()) {
            currentList.maxOf { it.id }.plus(1)
        } else {
            1
        }
        val dog = Dog(
            id = id,
            name = dogInput.name,
            birthDateAmerican = dogInput.birthDate,
            birthDateInternational = dogInput.birthDate.dateToNewFormat(DateFormat.INTERNATIONAL),
            weightInLb = dogInput.weight.toDouble().formattedToTwoDecimals(),
            weightInKg = dogInput.weight.toDouble().toNewWeight(WeightFormat.KILOGRAMS),
            weightFormat = dogInput.weightFormat,
            dogYears = dogInput.birthDate.toDogYears(dateFormat = dogInput.dateFormat),
            humanYears = dogInput.birthDate.toHumanYears(dateFormat = dogInput.dateFormat),
            profilePic = dogInput.profilePic,
            dateFormat = dogInput.dateFormat,
            shouldAnimate = true
        )
        dogsDataSource.addDogs(dog)
    }

    override suspend fun deleteDog(dog: Dog) {
        dogsDataSource.deleteDog(dog)
    }

    override suspend fun updateDog(dog: Dog) {
        dogsDataSource.updateDogs(dog)
    }

    override suspend fun clear() {
        dogsDataSource.clear()
    }
}
