package com.sidgowda.pawcalc.data.dogs

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.model.toDog
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.db.dog.DogEntity
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DogsMemoryDataSourceTest {

    private lateinit var dogsDataSource: DogsDataSource
    private lateinit var settingsDataSource: SettingsDataSource
    private lateinit var testCoroutineDispatcher: CoroutineDispatcher

    @Before
    fun setup() {
        settingsDataSource = mockk()
        testCoroutineDispatcher = StandardTestDispatcher()
        every { settingsDataSource.settings() } returns flowOf(
            Settings(
                weightFormat = WeightFormat.POUNDS,
                dateFormat = DateFormat.AMERICAN,
                themeFormat = ThemeFormat.SYSTEM
            )
        )
        dogsDataSource = DogsMemoryDataSource(settingsDataSource, testCoroutineDispatcher)
    }

    @Test
    fun `assert MemoryDataSource returns null if it has no dogs`() = runTest {
        val dogs = dogsDataSource.dogs().first()
        assertNull(dogs)
    }

    @Test
    fun `when dog is added, it should have dogYears and humanYears as well`() = runTest {
        dogsDataSource.addDogs(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_TWO_ENTITY.toDog())

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE_ENTITY.toDog(),
                    DOG_TWO_ENTITY.toDog()
                ), awaitItem()
            )
        }
    }

    @Test
    fun `add method should be able to add multiple dogs at a time`() = runTest {
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
        }
    }

    @Test
    fun `when dog two is deleted, only dog one and dog three exists`() = runTest {
        dogsDataSource.addDogs(
            DOG_ONE_ENTITY.toDog(),
            DOG_TWO_ENTITY.toDog(),
            DOG_THREE_ENTITY.toDog()
        )
        dogsDataSource.deleteDog(DOG_TWO_ENTITY.toDog())

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE_ENTITY.toDog(),
                    DOG_THREE_ENTITY.toDog()
                ), awaitItem()
            )
        }
    }

    @Test
    fun `when update is called for dog two, then dog two is updated`() = runTest {
        dogsDataSource.addDogs(DOG_ONE_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_TWO_ENTITY.toDog())
        dogsDataSource.addDogs(DOG_THREE_ENTITY.toDog())
        dogsDataSource.updateDog(DOG_TWO_ENTITY.copy(name = "Updated Name").toDog())

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE_ENTITY.toDog(),
                    DOG_TWO_ENTITY.copy(name = "Updated Name").toDog(),
                    DOG_THREE_ENTITY.toDog()
                ), awaitItem()
            )
        }
    }

    @Test
    fun `when clear is called, all dogs should be deleted and null should be returned`() = runTest {
        dogsDataSource.addDogs(
            DOG_ONE_ENTITY.toDog(),
            DOG_TWO_ENTITY.toDog(),
            DOG_THREE_ENTITY.toDog()
        )

        dogsDataSource.clear()

        val dogs = dogsDataSource.dogs().first()
        assertNull(dogs)
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
        val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    }
}
