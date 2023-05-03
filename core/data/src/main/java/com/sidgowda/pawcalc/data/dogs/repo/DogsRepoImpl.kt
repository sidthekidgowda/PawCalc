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

    private val loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    override fun dogState(): Flow<DogState> = combine(
        memory.dogs(),
        loadState
    ) { dogs, loadState ->
        when (loadState) {
            LoadState.Loading -> DogState(
                isLoading = true,
                dogs = dogs
            )
            LoadState.Idle -> DogState(
                isLoading = false,
                dogs = dogs
            )
        }
    }
    .onEach { dogState ->
        // update disk for next session to use updated weight and date format
        if (dogState.dogs.isNotEmpty()) {
            disk.updateDog(*dogState.dogs.toTypedArray())
        }
    }
    // buffer ensures onEach is emitted on a different coroutine as collect
    .buffer()
    .flowOn(computationDispatcher)
    .distinctUntilChanged()

    override suspend fun fetchDogs() {
        // if dogs exists in memory, do nothing
        val inMemoryDogs = memory.dogs().first()
        if (inMemoryDogs.isNotEmpty()) {
            loadState.update { LoadState.Idle }
            // add log statement
        } else {
            loadState.update { LoadState.Loading }
            try {
                val inDiskDogs = disk.dogs().first()
                if (inDiskDogs.isNotEmpty()) {
                   memory.addDogs(*inDiskDogs.toTypedArray())
                }
            } catch (e: Exception) {
                // todo add log statement
            } finally {
                loadState.update { LoadState.Idle }
            }
        }
    }

    override suspend fun addDog(dogInput: DogInput) {
        val dogs = memory.dogs().first()
        val id = if (dogs.isNotEmpty()) {
            dogs.maxOf { it.id }.plus(1)
        } else {
            1
        }
        val dog = Dog(
            id = id,
            name = dogInput.name,
            birthDate = dogInput.birthDate,
            dateFormat = dogInput.dateFormat,
            weight = dogInput.weight.toDouble(),
            weightFormat = dogInput.weightFormat,
            dogYears = dogInput.birthDate.toDogYears(dateFormat = dogInput.dateFormat),
            humanYears = dogInput.birthDate.toHumanYears(dateFormat = dogInput.dateFormat),
            profilePic = dogInput.profilePic
        )
        memory.addDogs(dog)
        disk.addDogs(dog)
    }

    override suspend fun deleteDog(dog: Dog) {
        memory.deleteDog(dog)
        disk.deleteDog(dog)
    }

    override suspend fun updateDog(dog: Dog) {
        memory.updateDog(dog)
        disk.updateDog(dog)
    }

    override suspend fun clear() {
        memory.clear()
        disk.clear()
    }
}
