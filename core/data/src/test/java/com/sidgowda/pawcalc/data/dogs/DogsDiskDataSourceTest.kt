package com.sidgowda.pawcalc.data.dogs

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDiskDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toDog
import com.sidgowda.pawcalc.db.dog.DogEntity
import com.sidgowda.pawcalc.db.dog.DogsDao
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DogsDiskDataSourceTest {

    private lateinit var dogsDataSource: DogsDataSource
    private lateinit var dogsDao: DogsDao

    @Before
    fun setup() {
        dogsDao = mockk()
        dogsDataSource = DogsDiskDataSource(dogsDao)
    }

    @Test
    fun `assertDiskDataSourceIsEmpty`() = runTest {
        coEvery { dogsDao.dogs() } returns flowOf(emptyList())

        dogsDataSource.dogs().test {
            assertEquals(emptyList<DogEntity>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when dog is added, it should have dogYears and humanYears as well`() = runTest {
        val listOfDogs = mutableListOf<DogEntity>()
        coEvery { dogsDao.dogs() } returns flow {
            emit(listOfDogs)
        }
        coEvery { dogsDao.addDog(any()) } answers {
            val dog = firstArg<DogEntity>()
            listOfDogs.add(dog)
        }

        dogsDataSource.addDog(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDog(DOG_TWO_ENTITY.toDog())

        dogsDataSource.dogs().test {
            assertEquals(listOf(DOG_ONE_ENTITY.toDog(), DOG_TWO_ENTITY.toDog()), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when dog two is deleted, only dog one and dog three exists`() = runTest {
        val listOfDogs = mutableListOf<DogEntity>()
        coEvery { dogsDao.dogs() } returns flow {
            emit(listOfDogs)
        }
        coEvery { dogsDao.addDog(any()) } answers {
            val dog = firstArg<DogEntity>()
            listOfDogs.add(dog)
        }
        coEvery { dogsDao.deleteDog(any()) } answers {
            val dog = firstArg<DogEntity>()
            listOfDogs.remove(dog)
        }
        dogsDataSource.addDog(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDog(DOG_TWO_ENTITY.toDog())
        dogsDataSource.addDog(DOG_THREE_ENTITY.toDog())
        dogsDataSource.deleteDog(DOG_TWO_ENTITY.toDog())

        dogsDataSource.dogs().test {
            assertEquals(listOf(DOG_ONE_ENTITY.toDog(), DOG_THREE_ENTITY.toDog()), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when update is called for dog two, then dog two is updated`() = runTest {
        val listOfDogs = mutableListOf<DogEntity>()
        coEvery { dogsDao.dogs() } returns flow {
            emit(listOfDogs)
        }
        coEvery { dogsDao.addDog(any()) } answers {
            val dog = firstArg<DogEntity>()
            listOfDogs.add(dog)
        }
        coEvery { dogsDao.updateDog(any()) } answers {
            val dog = firstArg<DogEntity>()
            val indexToReplace = listOfDogs.indexOfFirst { oldDog -> dog.id == oldDog.id }
            if (indexToReplace != -1) {
                listOfDogs[indexToReplace] = dog
            }
        }
        dogsDataSource.addDog(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDog(DOG_TWO_ENTITY.toDog())
        dogsDataSource.addDog(DOG_THREE_ENTITY.toDog())
        dogsDataSource.updateDog(DOG_TWO_ENTITY.copy(name = "Updated Name").toDog())

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE_ENTITY.toDog(),
                    DOG_TWO_ENTITY.copy(name = "Updated Name").toDog(),
                    DOG_THREE_ENTITY.toDog()
                ), awaitItem()
            )
            awaitComplete()
        }
    }

    @Test
    fun `when clear is called, all dogs should be deleted`() = runTest {
        val listOfDogs = mutableListOf<DogEntity>()
        coEvery { dogsDao.dogs() } returns flow {
            emit(listOfDogs)
        }
        coEvery { dogsDao.addDog(any()) } answers {
            val dog = firstArg<DogEntity>()
            listOfDogs.add(dog)
        }
        coEvery { dogsDao.deleteAll() } answers {
            listOfDogs.clear()
        }
        dogsDataSource.addDog(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDog(DOG_TWO_ENTITY.toDog())
        dogsDataSource.addDog(DOG_THREE_ENTITY.toDog())

        dogsDataSource.clear()

        dogsDataSource.dogs().test {
            assertEquals(emptyList<Dog>(), awaitItem())
            awaitComplete()
        }
    }

    private companion object {
        val DOG_ONE_ENTITY = DogEntity(
            id = 1,
            name = "Dog",
            weight = 65.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/22/2021"
        )
        val DOG_TWO_ENTITY = DogEntity(
            id = 2,
            name = "Dog",
            weight = 65.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/12/2021"
        )
        val DOG_THREE_ENTITY = DogEntity(
            id = 3,
            name = "Dog",
            weight = 65.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/12/2021"
        )
    }
}
