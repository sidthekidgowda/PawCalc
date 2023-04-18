package com.sidgowda.pawcalc.data.dogs.repo

import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.data.dogs.model.DogState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

class DogsRepoImpl @Inject constructor(
    @Named("memory") private val memory: DogsDataSource,
    @Named("disk") private val disk: DogsDataSource,
    private val computationDispatcher: CoroutineDispatcher
) : DogsRepo {

    private sealed class LoadState {
        object Loading : LoadState()
        object Idle : LoadState()
    }

    private val loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    override fun dogState(): Flow<DogState> = combine(
        memory.dogs(),
        loadState
    ) { dogs, loadState ->
        when (loadState) {
            LoadState.Loading -> DogState(
                isLoading = true,
                dogs = emptyList()
            )
            LoadState.Idle -> DogState(
                isLoading = false,
                dogs = dogs ?: emptyList()
            )
        }
    }
    .flowOn(computationDispatcher)
    .distinctUntilChanged()

    override suspend fun fetchDogs() {
        // if dogs exists in memory, do nothing
        val inMemoryDogs = memory.dogs().first()
        if (inMemoryDogs != null) {
            // add log statement
        } else {
            loadState.update { LoadState.Loading }
            try {
                val inDiskDogs = disk.dogs().first()
                if (inDiskDogs != null) {
                   memory.clear()
                   memory.addDog(*inDiskDogs.toTypedArray())
                }
            } catch (e: Exception) {
                // add log statement
            } finally {
                loadState.update { LoadState.Idle }
            }
        }
    }

    override suspend fun addDog(dogInput: DogInput) {
        // start with 1 if no dogs have been added, other wise increment
        val id = (memory.dogs().first()?.size?.plus(1)) ?: 1
        val dog = Dog(
            id = id,
            isLoading = false,
            name = dogInput.name,
            birthDate = dogInput.birthDate,
            weight = dogInput.weight.toDouble(),
            dogYears = dogInput.birthDate.toDogYears(),
            humanYears = dogInput.birthDate.toHumanYears(),
            profilePic = dogInput.profilePic
        )
        memory.addDog(dog)
        disk.addDog(dog)
    }

    override suspend fun deleteDog(dog: Dog) {
        memory.deleteDog(dog)
        disk.deleteDog(dog)
    }

    override suspend fun updateDog(dog: Dog) {
        val updatedDog = dog.copy(isLoading = true)
        memory.updateDog(updatedDog)
        disk.updateDog(updatedDog)
    }

    override suspend fun clear() {
        memory.clear()
        disk.clear()
    }
}
