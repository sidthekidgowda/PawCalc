package com.sidgowda.pawcalc.domain.dogs

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.DogState
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
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
    private lateinit var dogsRepo: DogsRepo

    @Before
    fun setup() {
        dogsRepo = mockk()
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        getDogForIdUseCase = GetDogForIdUseCase(dogsRepo, testDispatcher)
    }

    @Test
    fun `when dogRepo is loading, no dogs should be emitted`() = testScope.runTest {
        coEvery { dogsRepo.dogState() } returns flowOf(
            DogState(isLoading = true, dogs = emptyList())
        )
        getDogForIdUseCase.invoke(1).test {
            expectNoEvents()
        }
    }

    @Test
    fun `when dogRepo is loading, and dogs exist, no dogs should be emitted`() = testScope.runTest {
        coEvery { dogsRepo.dogState() } returns flowOf(
            DogState(isLoading = true, dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        )
        getDogForIdUseCase.invoke(1).test {
            expectNoEvents()
        }
    }
    @Test
    fun `when dogRepo is not loading, and no dogs exist, it should not emit any dogs`() = testScope.runTest {
        coEvery { dogsRepo.dogState() } returns flowOf(
            DogState(isLoading = false, dogs = emptyList())
        )
        getDogForIdUseCase.invoke(1).test {
            expectNoEvents()
        }
    }

    @Test
    fun `when dogRepo is not loading, and dogs exist, it should find dog for id 1`() = testScope.runTest {
        coEvery { dogsRepo.dogState() } returns flowOf(
            DogState(isLoading = false, dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        )
        getDogForIdUseCase.invoke(1).test {
            assertEquals(DOG_ONE, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when dogRepo is not loading, and dogs exist, it should find dog for id 2`() = testScope.runTest {
        coEvery { dogsRepo.dogState() } returns flowOf(
            DogState(isLoading = false, dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        )
        getDogForIdUseCase.invoke(2).test {
            assertEquals(DOG_TWO, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when dogRepo is not loading, and dogs exist, it should find dog for id 3`() = testScope.runTest {
        coEvery { dogsRepo.dogState() } returns flowOf(
            DogState(isLoading = false, dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        )
        getDogForIdUseCase.invoke(3).test {
            assertEquals(DOG_THREE, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when dogRepo is not loading, and dogs exist, it should not find dog for id 4`() = testScope.runTest {
        coEvery { dogsRepo.dogState() } returns flowOf(
            DogState(isLoading = false, dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        )
        getDogForIdUseCase.invoke(4).test {
           expectNoEvents()
        }
    }

    @Test
    fun `when dogRepo throws error, then error should be collected`() = testScope.runTest {
        coEvery { dogsRepo.dogState() } returns flow {
           throw IllegalStateException("Dog Repo error")
        }
        getDogForIdUseCase.invoke(3).test {
            assertEquals("Dog Repo error", awaitError().message)
        }
    }

    private companion object {
        val DOG_ONE = Dog(
            id = 1,
            profilePic = Uri.EMPTY,
            name = "dog_1",
            weight = 84.0,
            birthDate = "12/20/1999",
            dogYears =  "12/20/1999".toDogYears(),
            humanYears = "12/20/1999".toHumanYears()
        )

        val DOG_TWO = Dog(
            id = 2,
            profilePic = Uri.EMPTY,
            name = "dog_2",
            weight = 84.0,
            birthDate = "12/20/1999",
            dogYears =  "12/2/1999".toDogYears(),
            humanYears = "12/20/1999".toHumanYears()
        )

        val DOG_THREE = Dog(
            id = 3,
            profilePic = Uri.EMPTY,
            name = "dog_3",
            weight = 84.0,
            birthDate = "12/20/1999",
            dogYears =  "12/20/1999".toDogYears(),
            humanYears = "12/20/1999".toHumanYears()
        )
    }

}
