package com.sidgowda.pawcalc.data.dogs

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.DogState
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepoImpl
import com.sidgowda.pawcalc.db.dog.DogsDao
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DogsRepoTest {

    private object DogsDiskDataSource : DogsDataSource {

        val listOfDogs = mutableListOf<Dog>()

        override fun dogs(): Flow<List<Dog>?> {
            return flow {
                emit(listOfDogs)
            }
        }

        override suspend fun addDog(vararg dog: Dog) {
           listOfDogs.addAll(dog)
        }

        override suspend fun deleteDog(dog: Dog) {
            listOfDogs.remove(dog)
        }

        override suspend fun updateDog(dog: Dog) {
            val indexToReplace = listOfDogs.indexOfFirst { oldDog -> dog.id == oldDog.id }
            if (indexToReplace != -1) {
                listOfDogs[indexToReplace] = dog
            }
        }

        override suspend fun clear() {
            listOfDogs.clear()
        }

    }

    private lateinit var dogsRepo: DogsRepo
    private lateinit var dogsDao: DogsDao
    private lateinit var dogsMemoryDataSource: DogsDataSource
    private lateinit var dogsDiskDataSource: DogsDataSource
    private lateinit var testCoroutineDispatcher: CoroutineDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testCoroutineDispatcher = UnconfinedTestDispatcher()
        testScope = TestScope(testCoroutineDispatcher)
        DogsDiskDataSource.listOfDogs.clear()
        dogsDiskDataSource = DogsDiskDataSource
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
    fun `when fetch dogs is called and no dogs are there in disk and cache, then loading and empty list is emitted`() = testScope.runTest {
        val history = dogsRepo.createStateHistory()
        dogsRepo.fetchDogs()

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

    @Test
    fun `when dogs exist in disk, then verify dogs are emitted from disk`() = testScope.runTest {
        dogsDiskDataSource.addDog(
            DOG_ONE, DOG_TWO
        )
        val spyDisk = spyk(dogsDiskDataSource)
        dogsRepo = DogsRepoImpl(
            memory = dogsMemoryDataSource,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )
        val history = dogsRepo.createStateHistory()
        dogsRepo.fetchDogs()

        history shouldContainExactly listOf(
            DogState(
                isLoading = true,
                dogs = emptyList()
            ),
            DogState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE, DOG_TWO
                )
            )
        )
        // verify disk is used for dogs
        verify(exactly = 1) { spyDisk.dogs() }
    }

    // verify disk isn't used

    private fun DogsRepo.createStateHistory(): List<DogState> {
        val history = mutableListOf<DogState>()
        testScope.backgroundScope.launch {
            dogState().toCollection(history)
        }
        return history
    }

    private companion object {
         val DOG_ONE = Dog(
             id = 1,
             name = "Dog_1",
             weight = 65.0,
             profilePic = Uri.EMPTY,
             birthDate = "12/21/2021",
             dogYears = "12/21/2021".toDogYears(),
             humanYears = "12/21/2021".toHumanYears()
         )
        val DOG_TWO = Dog(
            id = 2,
            name = "Dog_2",
            weight = 68.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/22/2021",
            dogYears = "12/22/2021".toDogYears(),
            humanYears = "12/22/2021".toHumanYears()
        )
        val DOG_THREE = Dog(
            id = 3,
            name = "Dog_3",
            weight = 68.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/23/2021",
            dogYears = "12/23/2021".toDogYears(),
            humanYears = "12/23/2021".toHumanYears()
        )
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
