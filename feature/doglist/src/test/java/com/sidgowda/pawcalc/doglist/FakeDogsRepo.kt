package com.sidgowda.pawcalc.doglist

import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.data.dogs.model.DogState
import com.sidgowda.pawcalc.data.dogs.model.formattedToTwoDecimals
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import kotlinx.coroutines.flow.*

class FakeDogsRepo(private val dogsDataSource: DogsDataSource) : DogsRepo {

    private val loadState = MutableStateFlow(true)
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
            birthDate = dogInput.birthDate,
            dateFormat = dogInput.dateFormat,
            weight = dogInput.weight.toDouble().formattedToTwoDecimals(),
            weightFormat = dogInput.weightFormat,
            dogYears = dogInput.birthDate.toDogYears(dateFormat = dogInput.dateFormat),
            humanYears = dogInput.birthDate.toHumanYears(dateFormat = dogInput.dateFormat),
            profilePic = dogInput.profilePic
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
