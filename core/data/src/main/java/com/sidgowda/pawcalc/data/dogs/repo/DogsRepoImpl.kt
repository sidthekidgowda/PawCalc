package com.sidgowda.pawcalc.data.dogs.repo

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.di.DogsRepoState
import com.sidgowda.pawcalc.data.dogs.model.Dog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    private val scope = CoroutineScope(SupervisorJob())

    override fun dogs(): Flow<DogsRepoState> = combine(
        memory.dogs(),
        loadState
    ) { dogs, loadState ->
        when (loadState) {
            LoadState.Loading -> DogsRepoState(
                isLoading = true,
                dogs = emptyList()
            )
            LoadState.Idle -> DogsRepoState(
                isLoading = false,
                dogs = if (dogs == null) emptyList() else dogs
            )
        }
    }.flowOn(Dispatchers.Default)
    .distinctUntilChanged()

    override suspend fun fetchDogs() {
        scope.launch {
            // if dogs exists in memory, do nothing
            val inMemoryDogs = memory.dogs().first()
            if (inMemoryDogs != null) {

            } else {
                // pull from disks

            }
            // if dogs exist in disk, use disk
        }
    }

    override suspend fun addDog(dog: Dog) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDog(dog: Dog) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDog(dog: Dog) {
        TODO("Not yet implemented")
    }

    // update job
    // delete job
    // insert job

    // VM states
    // loading
    // empty
    // error
    // data

    // VM events
    // update dog
    // insert dog

}
