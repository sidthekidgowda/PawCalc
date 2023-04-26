package com.sidgowda.pawcalc.data.dogs

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDiskDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.model.DogState
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepoImpl
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DogsRepoTest {

    private lateinit var dogsRepo: DogsRepo
    private lateinit var dogsMemoryDataSource: DogsMemoryDataSource
    private lateinit var dogsDiskDataSource: DogsDiskDataSource
    private lateinit var testCoroutineDispatcher: CoroutineDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testCoroutineDispatcher = UnconfinedTestDispatcher()
        testScope = TestScope(testCoroutineDispatcher)
        dogsDiskDataSource = mockk()
        dogsMemoryDataSource = DogsMemoryDataSource()
        dogsRepo = DogsRepoImpl(
            memory = dogsMemoryDataSource,
            disk = dogsDiskDataSource,
            computationDispatcher = testCoroutineDispatcher
        )
    }

    @Test
    fun `when no dogs are in memory then loading is emitted`() = testScope.runTest {
        val history = dogsRepo.createStateHistory()

        history shouldContainExactly listOf(
            DogState(
                isLoading = true,
                dogs = emptyList()
            )
        )
    }

    @Test
    fun `when fetch dogs is called and no dogs are there, then loading and empty list is emitted`() = testScope.runTest {
        val history = dogsRepo.createStateHistory()
        dogsRepo.fetchDogs()
        advanceUntilIdle()

        history shouldContainExactly listOf(
            DogState(
                isLoading = true,
                dogs = emptyList()
            ),
            DogState(
                isLoading = false,
                dogs = emptyList()
            )
        )
    }

    private fun DogsRepo.createStateHistory(): List<DogState> {
        val history = mutableListOf<DogState>()
        testScope.backgroundScope.launch {
            dogState().toCollection(history)
        }
        return history
    }


//    fun dogState(): Flow<DogState>
//
//    suspend fun fetchDogs()
//
//    suspend fun addDog(dogInput: DogInput)
//
//    suspend fun deleteDog(dog: Dog)
//
//    suspend fun updateDog(dog: Dog)
//
//    suspend fun clear()

}
