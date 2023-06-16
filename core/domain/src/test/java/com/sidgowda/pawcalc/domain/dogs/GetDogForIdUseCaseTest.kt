package com.sidgowda.pawcalc.domain.dogs

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class GetDogForIdUseCaseTest {

    private lateinit var getDogForIdUseCase: GetDogForIdUseCase
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    private lateinit var dogsDataSource: DogsMemoryDataSource

    @Before
    fun setup() {
        dogsDataSource = mockk()
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        getDogForIdUseCase = GetDogForIdUseCase(dogsDataSource, testDispatcher)
    }

    @Test
    fun `when no dogs exist, it should not emit any dogs`() = testScope.runTest {
        coEvery { dogsDataSource.dogs() } returns flowOf(
            emptyList()
        )
        getDogForIdUseCase.invoke(1).test {
            expectNoEvents()
        }
    }

    @Test
    fun `when dog one exists, it should find dog for id 1`() = testScope.runTest {
        coEvery { dogsDataSource.dogs()  } returns flowOf(
            listOf(DOG_ONE, DOG_TWO, DOG_THREE)
        )
        getDogForIdUseCase.invoke(1).test {
            assertEquals(DOG_ONE, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when dog 2 exists, it should find dog for id 2`() = testScope.runTest {
        coEvery { dogsDataSource.dogs()  } returns flowOf(
            listOf(DOG_ONE, DOG_TWO, DOG_THREE)
        )
        getDogForIdUseCase.invoke(2).test {
            assertEquals(DOG_TWO, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when dog 3 exists it should find dog for id 3`() = testScope.runTest {
        coEvery { dogsDataSource.dogs()  } returns flowOf(
            listOf(DOG_ONE, DOG_TWO, DOG_THREE)
        )
        getDogForIdUseCase.invoke(3).test {
            assertEquals(DOG_THREE, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when there is no dog 4, it should not find dog for id 4`() = testScope.runTest {
        coEvery { dogsDataSource.dogs()  } returns flowOf(
            listOf(DOG_ONE, DOG_TWO, DOG_THREE)
        )
        getDogForIdUseCase.invoke(4).test {
           expectNoEvents()
        }
    }

    @Test
    fun `when dog data source throws error, then error should be collected`() = testScope.runTest {
        coEvery { dogsDataSource.dogs() } returns flow {
           throw IllegalStateException("Dog Data source error")
        }
        getDogForIdUseCase.invoke(3).test {
            assertEquals("Dog Data source error", awaitError().message)
        }
    }

    @Test
    fun `when weight format is changed, then collecting dog 1 should return new weight format`() = testScope.runTest {
        coEvery { dogsDataSource.dogs() } answers {
            flowOf(listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        } andThenAnswer {
            flowOf(listOf(
                DOG_ONE.copy(
                    weightFormat = WeightFormat.KILOGRAMS
                ),
                DOG_TWO.copy(
                    weightFormat = WeightFormat.KILOGRAMS
                ),
                DOG_THREE.copy(
                    weightFormat = WeightFormat.KILOGRAMS
                )
            ))
        }

        getDogForIdUseCase.invoke(1).test {
            assertEquals(DOG_ONE, awaitItem())
            awaitComplete()
        }
        getDogForIdUseCase.invoke(1).test {
            assertEquals(
                DOG_ONE.copy(
                    weightFormat = WeightFormat.KILOGRAMS
                ), awaitItem()
            )
            awaitComplete()
        }
    }

    @Test
    fun `when date format is changed, then collecting dog 2 should return new date format`() = testScope.runTest {
        coEvery { dogsDataSource.dogs() } answers {
            flowOf(listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        } andThenAnswer {
            flowOf(
                listOf(
                    DOG_ONE.copy(
                        dateFormat = DateFormat.INTERNATIONAL
                    ),
                    DOG_TWO.copy(
                        dateFormat = DateFormat.INTERNATIONAL
                    ),
                    DOG_THREE.copy(
                        dateFormat = DateFormat.INTERNATIONAL
                    )
                )
            )
        }

        getDogForIdUseCase.invoke(2).test {
            assertEquals(DOG_TWO, awaitItem())
            awaitComplete()
        }
        getDogForIdUseCase.invoke(2).test {
            assertEquals(
                DOG_TWO.copy(
                    dateFormat = DateFormat.INTERNATIONAL
                ), awaitItem()
            )
            awaitComplete()
        }
    }

    // add test settings changed, dogs should be changed

    private companion object {
        val DOG_ONE = Dog(
            id = 1,
            profilePic = Uri.EMPTY,
            name = "dog_1",
            weightInLb = 84.0,
            weightInKg = 84.0.toNewWeight(WeightFormat.KILOGRAMS),
            birthDateAmerican = "12/20/1999",
            birthDateInternational = "20/12/1999",
            dogYears =  "12/20/1999".toDogYears(),
            humanYears = "12/20/1999".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )

        val DOG_TWO = Dog(
            id = 2,
            profilePic = Uri.EMPTY,
            name = "dog_2",
            weightInLb = 84.0,
            weightInKg = 84.0.toNewWeight(WeightFormat.KILOGRAMS),
            birthDateAmerican = "12/20/1999",
            birthDateInternational = "20/12/1999",
            dogYears =  "12/20/1999".toDogYears(),
            humanYears = "12/20/1999".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )

        val DOG_THREE = Dog(
            id = 3,
            profilePic = Uri.EMPTY,
            name = "dog_3",
            weightInLb = 84.0,
            weightInKg = 84.0.toNewWeight(WeightFormat.KILOGRAMS),
            birthDateAmerican = "12/20/1999",
            birthDateInternational = "20/12/1999",
            dogYears =  "12/20/1999".toDogYears(),
            humanYears = "12/20/1999".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
    }

}
