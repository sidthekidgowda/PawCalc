package com.sidgowda.pawcalc.data.dogs

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        settingsDataSource = mockk()
        testCoroutineDispatcher = StandardTestDispatcher()
        testScope = TestScope(testCoroutineDispatcher)
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
    fun `assert MemoryDataSource returns nothing if it has no dogs`() = testScope.runTest {
        dogsDataSource.dogs().test {
            expectNoEvents()
        }
    }

    @Test
    fun `when dog is added, it should have dogYears and humanYears as well`() = testScope.runTest {
        dogsDataSource.addDogs(DOG_ONE)
        dogsDataSource.addDogs(DOG_TWO)

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE,
                    DOG_TWO
                ), awaitItem()
            )
        }
    }

    @Test
    fun `add method should be able to add multiple dogs at a time`() = testScope.runTest {
        dogsDataSource.addDogs(
            DOG_ONE,
            DOG_TWO,
            DOG_THREE
        )

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE,
                    DOG_TWO,
                    DOG_THREE
                ), awaitItem()
            )
        }
    }

    @Test
    fun `when dog two is deleted, only dog one and dog three exists`() = testScope.runTest {
        dogsDataSource.addDogs(
            DOG_ONE,
            DOG_TWO,
            DOG_THREE
        )
        dogsDataSource.deleteDog(DOG_TWO)

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE,
                    DOG_THREE
                ), awaitItem()
            )
        }
    }

    @Test
    fun `when update is called for dog two, then dog two is updated`() = testScope.runTest {
        dogsDataSource.addDogs(DOG_ONE)
        dogsDataSource.addDogs(DOG_TWO)
        dogsDataSource.addDogs(DOG_THREE)
        dogsDataSource.updateDog(DOG_TWO.copy(name = "Updated Name"))

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE,
                    DOG_TWO.copy(name = "Updated Name"),
                    DOG_THREE
                ), awaitItem()
            )
        }
    }

    @Test
    fun `when update is called with multiple dogs, then multiple dogs should be updated`() = testScope.runTest {
        dogsDataSource.addDogs(DOG_ONE)
        dogsDataSource.addDogs(DOG_TWO)
        dogsDataSource.addDogs(DOG_THREE)

        dogsDataSource.updateDog(
            DOG_TWO.copy(name = "dog_two_update"),
            DOG_ONE.copy(name = "dog_one_update")
        )

        dogsDataSource.dogs().test {
            assertEquals(
                listOf(
                    DOG_ONE.copy(name = "dog_one_update"),
                    DOG_TWO.copy(name = "dog_two_update"),
                    DOG_THREE
                ), awaitItem()
            )
        }

    }
    // transform weight
    // transform date

    @Test
    fun `when clear is called, all dogs should be deleted and null should be returned`() = testScope.runTest {
        dogsDataSource.addDogs(
            DOG_ONE,
            DOG_TWO,
            DOG_THREE
        )

        dogsDataSource.clear()

        dogsDataSource.dogs().test {
            assertEquals(emptyList<Dog>(), awaitItem())
        }
    }

    private companion object {
        val DOG_ONE = Dog(
            id = 1,
            name = "Dog",
            weight = 65.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/22/2021",
            dogYears = "12/22/2021".toDogYears(),
            humanYears = "12/22/2021".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        val DOG_TWO = Dog(
            id = 2,
            name = "Dog",
            weight = 65.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/12/2021",
            dogYears = "12/12/2021".toDogYears(),
            humanYears = "12/12/2021".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        val DOG_THREE = Dog(
            id = 3,
            name = "Dog",
            weight = 65.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/12/2021",
            dogYears = "12/12/2021".toDogYears(),
            humanYears = "12/12/2021".toHumanYears(),
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
