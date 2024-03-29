package com.sidgowda.pawcalc.data.dogs.datasource

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.dogs.mapInPlace
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
    fun `assert DiskDataSource returns empty if it has no dogs`() = runTest {
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
        coEvery { dogsDao.addDog(*anyVararg()) } answers {
            val dog = firstArg<Array<DogEntity>>()
            listOfDogs.addAll(dog)
        }

        dogsDataSource.addDogs(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_TWO_ENTITY.toDog())

        dogsDataSource.dogs().test {
            assertEquals(listOf(DOG_ONE_ENTITY.toDog(), DOG_TWO_ENTITY.toDog()), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `add method should be able to add multiple dogs at a time`() = runTest {
        val listOfDogs = mutableListOf<DogEntity>()
        coEvery { dogsDao.dogs() } returns flow {
            emit(listOfDogs)
        }
        coEvery { dogsDao.addDog(*anyVararg()) } answers {
            val dog = firstArg<Array<DogEntity>>()
            listOfDogs.addAll(dog)
        }
        dogsDataSource.addDogs(
            DOG_ONE_ENTITY.toDog(),
            DOG_TWO_ENTITY.toDog(),
            DOG_THREE_ENTITY.toDog()
        )

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE_ENTITY.toDog(),
                    DOG_TWO_ENTITY.toDog(),
                    DOG_THREE_ENTITY.toDog()
                ), awaitItem()
            )
            awaitComplete()
        }
    }

    @Test
    fun `when dog two is deleted, only dog one and dog three exists`() = runTest {
        val listOfDogs = mutableListOf<DogEntity>()
        coEvery { dogsDao.dogs() } returns flow {
            emit(listOfDogs)
        }
        coEvery { dogsDao.addDog(*anyVararg()) } answers {
            val dog = firstArg<Array<DogEntity>>()
            listOfDogs.addAll(dog)
        }
        coEvery { dogsDao.deleteDog(any()) } answers {
            val dog = firstArg<DogEntity>()
            listOfDogs.remove(dog)
        }
        dogsDataSource.addDogs(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_TWO_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_THREE_ENTITY.toDog())
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
        coEvery { dogsDao.addDog(*anyVararg()) } answers {
            val dog = firstArg<Array<DogEntity>>()
            listOfDogs.addAll(dog)
        }
        coEvery { dogsDao.updateDog(any()) } answers {
            val updatedDogs = firstArg<Array<DogEntity>>()
            val dogIdMap: Map<Int, DogEntity> = updatedDogs.associateBy { it.id }
            listOfDogs.mapInPlace {
                dogIdMap[it.id] ?: it
            }
        }
        dogsDataSource.addDogs(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_TWO_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_THREE_ENTITY.toDog())
        dogsDataSource.updateDogs(DOG_TWO_ENTITY.copy(name = "Updated Name").toDog())

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
    fun `when update is called for multiple dogs, then multiple dogs are updated`() = runTest {
        val listOfDogs = mutableListOf<DogEntity>()
        coEvery { dogsDao.dogs() } returns flow {
            emit(listOfDogs)
        }
        coEvery { dogsDao.addDog(*anyVararg()) } answers {
            val dog = firstArg<Array<DogEntity>>()
            listOfDogs.addAll(dog)
        }
        coEvery { dogsDao.updateDog(*anyVararg()) } answers {
            val updatedDogs = firstArg<Array<DogEntity>>()
            val dogIdMap: Map<Int, DogEntity> = updatedDogs.associateBy { it.id }
            listOfDogs.mapInPlace {
                dogIdMap[it.id] ?: it
            }
        }
        dogsDataSource.addDogs(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_TWO_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_THREE_ENTITY.toDog())
        dogsDataSource.updateDogs(
            DOG_TWO_ENTITY.copy(name = "Updated Name").toDog(),
            DOG_THREE_ENTITY.copy(name = "Updated Dog 3").toDog()
        )

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE_ENTITY.toDog(),
                    DOG_TWO_ENTITY.copy(name = "Updated Name").toDog(),
                    DOG_THREE_ENTITY.copy(name = "Updated Dog 3").toDog()
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
        coEvery { dogsDao.addDog(*anyVararg()) } answers {
            val dog = firstArg<Array<DogEntity>>()
            listOfDogs.addAll(dog)
        }
        coEvery { dogsDao.deleteAll() } answers {
            listOfDogs.clear()
        }
        dogsDataSource.addDogs(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_TWO_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_THREE_ENTITY.toDog())

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
            birthDate = "12/22/2021",
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        val DOG_TWO_ENTITY = DogEntity(
            id = 2,
            name = "Dog",
            weight = 65.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/12/2021",
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        val DOG_THREE_ENTITY = DogEntity(
            id = 3,
            name = "Dog",
            weight = 65.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/12/2021",
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
    }
}
