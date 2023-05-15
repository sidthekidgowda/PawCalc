package com.sidgowda.pawcalc.data.dogs.repo

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.data.dogs.model.DogState
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.test.fakes.FakeDogsDataSource
import com.sidgowda.pawcalc.test.fakes.FakeSettingsDataSource
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import junit.framework.TestCase.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DogsRepoTest {
    private lateinit var dogsRepo: DogsRepo
    private lateinit var dogsMemoryDataSource: DogsDataSource
    private lateinit var dogsDiskDataSource: DogsDataSource
    private lateinit var testCoroutineDispatcher: CoroutineDispatcher
    private lateinit var settingsDataSource: SettingsDataSource
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testCoroutineDispatcher = UnconfinedTestDispatcher()
        settingsDataSource = FakeSettingsDataSource()
        testScope = TestScope(testCoroutineDispatcher)
        dogsDiskDataSource = FakeDogsDataSource()
        dogsMemoryDataSource = DogsMemoryDataSource(settingsDataSource, testCoroutineDispatcher)
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
        dogsDiskDataSource.addDogs(
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
                isLoading = true,
                dogs = listOf(
                    DOG_ONE, DOG_TWO
                )
            ),
            DogState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE, DOG_TWO
                )
            )
        )
        verify(exactly = 1) { spyDisk.dogs() }
    }

    @Test
    fun `given dogs exist in disk, when subscribers collect, memory will get updated from disk and new subscribers will collect from memory`() = testScope.runTest {
        dogsDiskDataSource.addDogs(
            DOG_ONE, DOG_TWO
        )
        val spyDisk = spyk(dogsDiskDataSource)
        val spyMemory = spyk(dogsMemoryDataSource)
        dogsRepo = DogsRepoImpl(
            memory = spyMemory,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )
        var dogs = spyMemory.dogs().first()
        assertEquals(emptyList<Dog>(), dogs)
        dogsRepo.fetchDogs().also { advanceUntilIdle() }
        coVerify {
            spyDisk.dogs()
            spyMemory.addDogs(*anyVararg())
        }

        dogsRepo.fetchDogs().also { advanceUntilIdle() }
        verify {
            spyMemory.dogs()
        }
        dogs = spyMemory.dogs().first()
        assertNotNull(dogs)
    }

    @Test
    fun `given no dogs exist, when new dogs are added, then they are added to memory and disk`() = testScope.runTest {
        val spyDisk = spyk(dogsDiskDataSource)
        val spyMemory = spyk(dogsMemoryDataSource)
        dogsRepo = DogsRepoImpl(
            memory = spyMemory,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )
        dogsRepo.addDog(
            DogInput(
                profilePic = Uri.EMPTY,
                name = "dog",
                weight = 89.0.toString(),
                birthDate = "1/1/2021",
                weightFormat = WeightFormat.POUNDS,
                dateFormat = DateFormat.AMERICAN
            )
        ).also { advanceUntilIdle() }

        coVerify {
            spyMemory.addDogs(*anyVararg())
            spyDisk.addDogs(*anyVararg())
        }
    }

    @Test
    fun `given no dogs exist, when new dogs are added and collected, then they are collected from memory`() = testScope.runTest {
        val spyDisk = spyk(dogsDiskDataSource)
        val spyMemory = spyk(dogsMemoryDataSource)
        dogsRepo = DogsRepoImpl(
            memory = spyMemory,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )
        dogsRepo.addDog(
            DogInput(
                profilePic = Uri.EMPTY,
                name = "dog",
                weight = 89.0.toString(),
                birthDate = "1/1/2021",
                weightFormat = WeightFormat.POUNDS,
                dateFormat = DateFormat.AMERICAN
            )
        ).also { advanceUntilIdle() }

        dogsRepo.fetchDogs().also { advanceUntilIdle() }

        verify {
            spyMemory.dogs()
        }
        verify(exactly = 0) {
            spyDisk.dogs()
        }
    }

    @Test
    fun `when dogs are deleted, then they are deleted from memory and disk`() = testScope.runTest {
        val spyDisk = spyk(dogsDiskDataSource)
        val spyMemory = spyk(dogsMemoryDataSource)
        dogsRepo = DogsRepoImpl(
            memory = spyMemory,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )
        createDogInputs(2).forEach {
            dogsRepo.addDog(it)
        }
        dogsRepo.deleteDog(DOG_TWO).also { advanceUntilIdle() }

        coVerify {
            spyMemory.deleteDog(any())
            spyDisk.deleteDog(any())
        }
    }

    @Test
    fun `when dogs are deleted, then new subscribers should get updated dogs`() = testScope.runTest {
        createDogInputs(2).forEach {
            dogsRepo.addDog(it)
        }
        dogsRepo.deleteDog(DOG_ONE).also { advanceUntilIdle() }
        dogsRepo.fetchDogs().also { advanceUntilIdle() }

        dogsRepo.dogState().first().dogs shouldContainExactly listOf(
            DOG_TWO
        )
    }

    @Test
    fun `given 6 dogs, when dog 2 is deleted and a new dog is added, then should be added to end of list and it's id is Id 7`() = testScope.runTest {
        createDogInputs(6).forEach {
            dogsRepo.addDog(it)
        }
        dogsRepo.deleteDog(DOG_TWO)
        dogsRepo.addDog(
            DogInput(
                profilePic = Uri.EMPTY,
                name = "Dog_7",
                weight = 68.0.toString(),
                birthDate = "12/7/2021",
                weightFormat = WeightFormat.POUNDS,
                dateFormat = DateFormat.AMERICAN
            )
        ).also { advanceUntilIdle() }

        dogsRepo.dogState().first().dogs.last() shouldBe Dog(
            id = 7,
            name = "Dog_7",
            weight = 68.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/7/2021",
            dogYears = "12/7/2021".toDogYears(),
            humanYears = "12/7/2021".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
    }

    @Test
    fun `given 5 dogs, when dog 3 is updated, then it should be updated in memory and disk`() = testScope.runTest {
        val spyDisk = spyk(dogsDiskDataSource)
        val spyMemory = spyk(dogsMemoryDataSource)
        dogsRepo = DogsRepoImpl(
            memory = spyMemory,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )
        createDogInputs(5).forEach {
            dogsRepo.addDog(it)
        }
        dogsRepo.updateDog(DOG_THREE.copy( name = "Dog_3_Update")).also { advanceUntilIdle() }

        coVerify {
            spyMemory.updateDogs(any())
            spyDisk.updateDogs(any())
        }
    }

    @Test
    fun `when dog 3 is updated, it should be reflected in new state`() = testScope.runTest {
        dogsDiskDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE)
        val history = dogsRepo.createStateHistory()
        dogsRepo.fetchDogs()
        dogsRepo.updateDog(DOG_THREE.copy( name = "Dog_3_Update")).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogState(
                isLoading = true,
                dogs = emptyList()
            ),
            DogState(
                isLoading = true,
                dogs = listOf(
                    DOG_ONE, DOG_TWO, DOG_THREE
                )
            ),
            DogState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE, DOG_TWO, DOG_THREE
                )
            ),
            DogState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE, DOG_TWO, DOG_THREE.copy(name = "Dog_3_Update")
                )
            )
        )
    }

    @Test
    fun `when clear is called, then memory and disk should clear as well`() = testScope.runTest {
        val spyDisk = spyk(dogsDiskDataSource)
        val spyMemory = spyk(dogsMemoryDataSource)
        dogsRepo = DogsRepoImpl(
            memory = spyMemory,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )
        createDogInputs(6).forEach {
            dogsRepo.addDog(it)
        }
        dogsRepo.clear().also { advanceUntilIdle() }

        coVerify {
            spyMemory.clear()
            spyDisk.clear()
        }
    }

    @Test
    fun `given 6 dogs, when clear is called, then no dogs should be emitted`() = testScope.runTest {
        dogsDiskDataSource.addDogs(
            DOG_ONE,
            DOG_TWO,
            DOG_THREE,
            DOG_TWO.copy(id = 4),
            DOG_ONE.copy(id = 5),
            DOG_THREE.copy(id = 6)
        )
        val history = dogsRepo.createStateHistory()
        dogsRepo.fetchDogs()
        dogsRepo.clear().also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogState(
                isLoading = true,
                dogs = emptyList()
            ),
            DogState(
                isLoading = true,
                dogs = listOf(
                    DOG_ONE,
                    DOG_TWO,
                    DOG_THREE,
                    DOG_TWO.copy(id = 4),
                    DOG_ONE.copy(id = 5),
                    DOG_THREE.copy(id = 6)
                )
            ),
            DogState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE,
                    DOG_TWO,
                    DOG_THREE,
                    DOG_TWO.copy(id = 4),
                    DOG_ONE.copy(id = 5),
                    DOG_THREE.copy(id = 6)
                )
            ),
            DogState(
                isLoading = false,
                dogs = emptyList()
            )
        )
    }

    @Test
    fun `when disk throws error, then no dogs should be emitted`() = testScope.runTest {
        val mockDisk = mockk<DogsDataSource>()
        coEvery { mockDisk.dogs() } returns flow {
            throw IOException()
        }
        dogsRepo = DogsRepoImpl(
            memory = dogsMemoryDataSource,
            disk = mockDisk,
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
                dogs = emptyList()
            )
        )
    }

    @Test
    fun `when weight format is changed to kilograms, memory should emit updated weight`() = testScope.runTest {
        dogsDiskDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE)
        val history = dogsRepo.createStateHistory()
        dogsRepo.fetchDogs().also { advanceUntilIdle() }
        settingsDataSource.updateSettings(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS))
            .also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogState(
                isLoading = true,
                dogs = emptyList()
            ),
            DogState(
                isLoading = true,
                listOf(
                    DOG_ONE,
                    DOG_TWO,
                    DOG_THREE
                )
            ),
            DogState(
                isLoading = false,
                listOf(
                    DOG_ONE,
                    DOG_TWO,
                    DOG_THREE
                )
            ),
            DogState(
                isLoading = false,
                listOf(
                    DOG_ONE.copy(
                        weightFormat = WeightFormat.KILOGRAMS,
                        weight = 68.0.toNewWeight(WeightFormat.KILOGRAMS)
                    ),
                    DOG_TWO.copy(
                        weightFormat = WeightFormat.KILOGRAMS,
                        weight = 68.0.toNewWeight(WeightFormat.KILOGRAMS)
                    ),
                    DOG_THREE.copy(
                        weightFormat = WeightFormat.KILOGRAMS,
                        weight = 68.0.toNewWeight(WeightFormat.KILOGRAMS)
                    )
                )
            )
        )
    }

    @Test
    fun `when weight format is changed to Kilograms, disk is updated as well`() = testScope.runTest {
        dogsDiskDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE)
        val spyDisk = spyk(dogsDiskDataSource)
        dogsRepo = DogsRepoImpl(
            memory = dogsMemoryDataSource,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )

        // start collecting
        dogsRepo.createStateHistory()
        dogsRepo.fetchDogs().also { advanceUntilIdle() }
        settingsDataSource.updateSettings(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS))
        advanceUntilIdle()

        coVerify {
            spyDisk.updateDogs(*anyVararg())
        }
        spyDisk.dogs().first() shouldContainExactly listOf(
            DOG_ONE.copy(
                weightFormat = WeightFormat.KILOGRAMS,
                weight = 68.0.toNewWeight(WeightFormat.KILOGRAMS)
            ),
            DOG_TWO.copy(
                weightFormat = WeightFormat.KILOGRAMS,
                weight = 68.0.toNewWeight(WeightFormat.KILOGRAMS)
            ),
            DOG_THREE.copy(
                weightFormat = WeightFormat.KILOGRAMS,
                weight = 68.0.toNewWeight(WeightFormat.KILOGRAMS)
            )
        )
    }

    @Test
    fun `when date format is changed then memory should emit updated date`() = testScope.runTest {
        dogsDiskDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE)
        val history = dogsRepo.createStateHistory()
        dogsRepo.fetchDogs().also { advanceUntilIdle() }
        settingsDataSource.updateSettings(DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL))

        history shouldContainExactly listOf(
            DogState(
                isLoading = true,
                dogs = emptyList()
            ),
            DogState(
                isLoading = true,
                listOf(
                    DOG_ONE,
                    DOG_TWO,
                    DOG_THREE
                )
            ),
            DogState(
                isLoading = false,
                listOf(
                    DOG_ONE,
                    DOG_TWO,
                    DOG_THREE
                )
            ),
            DogState(
                isLoading = false,
                listOf(
                    DOG_ONE.copy(
                        dateFormat = DateFormat.INTERNATIONAL,
                        birthDate = "1/12/2021"
                    ),
                    DOG_TWO.copy(
                        dateFormat = DateFormat.INTERNATIONAL,
                        birthDate = "2/12/2021"
                    ),
                    DOG_THREE.copy(
                        dateFormat = DateFormat.INTERNATIONAL,
                        birthDate = "3/12/2021"
                    )
                )
            )
        )
    }

    @Test
    fun `when date format is changed to international, disk should be updated as well`() = testScope.runTest {
        dogsDiskDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE)
        val spyDisk = spyk(dogsDiskDataSource)
        dogsRepo = DogsRepoImpl(
            memory = dogsMemoryDataSource,
            disk = spyDisk,
            computationDispatcher = testCoroutineDispatcher
        )

        // start collecting
        dogsRepo.createStateHistory()
        dogsRepo.fetchDogs().also { advanceUntilIdle() }
        settingsDataSource.updateSettings(DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL))
            .also { advanceUntilIdle() }

        coVerify {
            spyDisk.updateDogs(*anyVararg())
        }
        spyDisk.dogs().first() shouldContainExactly listOf(
            DOG_ONE.copy(
                dateFormat = DateFormat.INTERNATIONAL,
                birthDate = "1/12/2021"
            ),
            DOG_TWO.copy(
                dateFormat = DateFormat.INTERNATIONAL,
                birthDate = "2/12/2021"
            ),
            DOG_THREE.copy(
                dateFormat = DateFormat.INTERNATIONAL,
                birthDate = "3/12/2021"
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

    private fun createDogInputs(count: Int): List<DogInput> {
        val dogInputList = mutableListOf<DogInput>()
        for (i in 1..count) {
            dogInputList.add(
                DogInput(
                    profilePic = Uri.EMPTY,
                    name = "Dog_$i",
                    weight = 68.0.toString(),
                    birthDate = "12/$i/2021",
                    weightFormat = WeightFormat.POUNDS,
                    dateFormat = DateFormat.AMERICAN
                )
            )
        }
        return dogInputList
    }

    private companion object {
         val DOG_ONE = Dog(
             id = 1,
             name = "Dog_1",
             weight = 68.0,
             profilePic = Uri.EMPTY,
             birthDate = "12/1/2021",
             dogYears = "12/1/2021".toDogYears(),
             humanYears = "12/1/2021".toHumanYears(),
             weightFormat = WeightFormat.POUNDS,
             dateFormat = DateFormat.AMERICAN,
             shouldAnimate = true
         )
        val DOG_TWO = Dog(
            id = 2,
            name = "Dog_2",
            weight = 68.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/2/2021",
            dogYears = "12/2/2021".toDogYears(),
            humanYears = "12/2/2021".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
        val DOG_THREE = Dog(
            id = 3,
            name = "Dog_3",
            weight = 68.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/3/2021",
            dogYears = "12/3/2021".toDogYears(),
            humanYears = "12/3/2021".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
        val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    }
}
