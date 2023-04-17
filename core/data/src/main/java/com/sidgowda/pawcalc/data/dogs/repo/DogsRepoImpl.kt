package com.sidgowda.pawcalc.data.dogs.repo

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.di.DogState
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.data.dogs.model.toDog
import com.sidgowda.pawcalc.db.dog.DogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

class DogsRepoImpl @Inject constructor(
    @Named("memory") private val memory: DogsDataSource,
    @Named("disk") private val disk: DogsDataSource
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
    }.flowOn(Dispatchers.Default)
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
        val dog = DogEntity(
            name = dogInput.name,
            birthDate = dogInput.birthDate,
            weight = dogInput.weight.toDouble(),
            profilePic = dogInput.profilePic
        ).toDog("", "")
        // source of truth should be disk, since room will generate a primary key
        disk.addDog(dog)
        // disk should contain the new dog at end of list
        val dogFromDisk: Dog = disk.dogs().first()!!.last()
        memory.addDog(dogFromDisk)
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
}
