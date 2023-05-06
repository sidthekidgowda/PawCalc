package com.sidgowda.pawcalc.db

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.db.dog.DogEntity
import com.sidgowda.pawcalc.db.dog.DogsDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DogsDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PawCalcDatabase
    private lateinit var dogsDao: DogsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PawCalcDatabase::class.java
        ).build()
        dogsDao = database.dogDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun assertDatabaseIsEmpty() = runTest {
        dogsDao.dogs().test {
            assertEquals(emptyList<DogEntity>(), awaitItem())
        }
    }

    @Test
    fun givenEmptyDatabase_whenDogIsAdded_thenDatabaseShouldHaveOneDog() = runTest {
        dogsDao.dogs().test {
            assertEquals(emptyList<DogEntity>(), awaitItem())
            dogsDao.addDog(DOG_ONE)
            assertEquals(listOf(DOG_ONE), awaitItem())
        }
    }

    @Test
    fun whenTwoDogsAreAdded_thenDatabaseShouldHaveTwoDogs() = runTest {
        dogsDao.addDog(DOG_ONE)
        dogsDao.addDog(DOG_TWO)
        dogsDao.dogs().test {
            assertEquals(listOf(DOG_ONE, DOG_TWO), awaitItem())
        }
    }

    @Test
    fun whenDeleteDogThreeIsCalled_thenDatabaseShouldNotHaveDogThreeAnymore() = runTest {
        dogsDao.addDog(DOG_ONE)
        dogsDao.addDog(DOG_TWO)
        dogsDao.addDog(DOG_THREE)
        dogsDao.addDog(DOG_FOUR)
        dogsDao.deleteDog(DOG_THREE)

        dogsDao.dogs().test {
            assertEquals(listOf(DOG_ONE, DOG_TWO, DOG_FOUR), awaitItem())
        }
    }

    @Test
    fun whenUpdateDogTwoIsCalled_ThenDogTwoShouldBeUpdated() = runTest {
        dogsDao.addDog(DOG_ONE)
        dogsDao.addDog(DOG_TWO)
        dogsDao.addDog(DOG_FOUR)

        dogsDao.dogs().test {
            assertEquals(listOf(DOG_ONE, DOG_TWO, DOG_FOUR), awaitItem())
            val updatedDog = DOG_TWO.copy(name = "UpdatedName")
            dogsDao.updateDog(updatedDog)
            assertEquals(listOf(DOG_ONE, updatedDog, DOG_FOUR), awaitItem())
        }
    }

    @Test
    fun whenDogsHaveSameId_thenOnlyFirstDogShouldBeAdded() = runTest {
        dogsDao.addDog(DOG_ONE)
        dogsDao.addDog(DOG_FOUR.copy(id = 1))

        dogsDao.dogs().test {
            assertEquals(listOf(DOG_ONE), awaitItem())
        }
    }

    @Test
    fun whenDeleteAllIsCalled_thenDatabaseShouldHaveNoDogs() = runTest {
        dogsDao.addDog(DOG_ONE)
        dogsDao.addDog(DOG_TWO)
        dogsDao.addDog(DOG_THREE)
        dogsDao.addDog(DOG_FOUR)

        dogsDao.dogs().test {
            assertEquals(listOf(DOG_ONE, DOG_TWO, DOG_THREE, DOG_FOUR), awaitItem())
            dogsDao.deleteAll()
            assertEquals(emptyList<DogEntity>(), awaitItem())
        }
    }

    @Test
    fun whenHigherPrimaryKeysAreAddedBeforeLowerPrimaryKeys_thenDatabaseShouldSortInAscendingOrder() = runTest {
        dogsDao.addDog(DOG_FOUR)
        dogsDao.addDog(DOG_THREE)
        dogsDao.addDog(DOG_TWO)
        dogsDao.addDog(DOG_ONE)

        dogsDao.dogs().test {
            assertEquals(listOf(DOG_ONE, DOG_TWO, DOG_THREE, DOG_FOUR), awaitItem())
        }
    }

    @Test
    fun addShouldBeToAddMultipleDogsAtAtTime() = runTest {
        dogsDao.addDog(DOG_ONE, DOG_TWO, DOG_THREE, DOG_FOUR)

        dogsDao.dogs().test {
            assertEquals(listOf(DOG_ONE, DOG_TWO, DOG_THREE, DOG_FOUR), awaitItem())
        }
    }

    @Test
    fun whenWeightFormatIsUpdatedThenCollectingOnDogsShouldGetUpdatedWeightFormat() = runTest {
        dogsDao.addDog(DOG_TWO, DOG_THREE)
        dogsDao.updateDog(
            DOG_TWO.copy(
                weight = 29.48,
                weightFormat = WeightFormat.KILOGRAMS
            ),
            DOG_THREE.copy(
                weight = 29.48,
                weightFormat = WeightFormat.KILOGRAMS
            )

        )

        dogsDao.dogs().test {
            assertEquals(
                listOf(
                    DOG_TWO.copy(
                        weight = 29.48,
                        weightFormat = WeightFormat.KILOGRAMS
                    ),
                    DOG_THREE.copy(
                        weight = 29.48,
                        weightFormat = WeightFormat.KILOGRAMS
                    )
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun whenDateFormatIsUpdatedThenCollectingOnDogsShouldGetUpdatedDateFormat() = runTest {
        dogsDao.addDog(DOG_TWO, DOG_THREE)
        dogsDao.updateDog(
            DOG_TWO.copy(
                birthDate = "22/12/2021",
                dateFormat = DateFormat.INTERNATIONAL
            ),
            DOG_THREE.copy(
                birthDate = "28/2/2021",
                dateFormat = DateFormat.INTERNATIONAL
            )
        )

        dogsDao.dogs().test {
            assertEquals(
                listOf(
                    DOG_TWO.copy(
                        birthDate = "22/12/2021",
                        dateFormat = DateFormat.INTERNATIONAL
                    ),
                    DOG_THREE.copy(
                        birthDate = "28/2/2021",
                        dateFormat = DateFormat.INTERNATIONAL
                    )
                ),
                awaitItem()
            )
        }
    }


    private companion object {
        val DOG_ONE = DogEntity(
            id = 1,
            name = "Dog",
            weight = 65.0,
            weightFormat = WeightFormat.POUNDS,
            profilePic = Uri.EMPTY,
            birthDate = "12/22/2021",
            dateFormat = DateFormat.AMERICAN
        )
        val DOG_TWO = DogEntity(
            id = 2,
            name = "Dog",
            weight = 65.0,
            weightFormat = WeightFormat.POUNDS,
            profilePic = Uri.EMPTY,
            birthDate = "12/22/2021",
            dateFormat = DateFormat.AMERICAN
        )
        val DOG_THREE = DogEntity(
            id = 3,
            name = "Dog",
            weight = 65.0,
            weightFormat = WeightFormat.POUNDS,
            profilePic = Uri.EMPTY,
            birthDate = "12/22/2021",
            dateFormat = DateFormat.AMERICAN
        )
        val DOG_FOUR = DogEntity(
            id = 4,
            name = "Dog",
            weight = 65.0,
            weightFormat = WeightFormat.POUNDS,
            profilePic = Uri.EMPTY,
            birthDate = "12/22/2021",
            dateFormat = DateFormat.AMERICAN
        )
    }
}
