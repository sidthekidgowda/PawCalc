package com.sidgowda.pawcalc.doglist

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.doglist.model.DogListEvent
import com.sidgowda.pawcalc.doglist.model.DogListState
import com.sidgowda.pawcalc.doglist.model.NavigateEvent
import com.sidgowda.pawcalc.doglist.ui.DogListViewModel
import com.sidgowda.pawcalc.domain.onboarding.GetOnboardingStateUseCase
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DogListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var computationDispatcher: CoroutineDispatcher
    private lateinit var ioDispatcher: CoroutineDispatcher
    private lateinit var viewModel: DogListViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dogsRepo: DogsRepo
    private lateinit var dogsDataSource: DogsDataSource
    private lateinit var getOnboardingState: GetOnboardingStateUseCase

    @Before
    fun setup() {
        ioDispatcher = StandardTestDispatcher()
        computationDispatcher = StandardTestDispatcher()
        savedStateHandle = mockk()
        getOnboardingState = mockk()
        dogsDataSource = FakeDogsDataSource()
        dogsRepo = FakeDogsRepo(dogsDataSource)
        every { getOnboardingState.invoke() } returns flowOf(OnboardingState.Onboarded)
        viewModel = DogListViewModel(
            getOnboardingState = getOnboardingState,
            dogsRepo = dogsRepo,
            savedStateHandle = SavedStateHandle(),
            ioDispatcher = ioDispatcher,
            computationDispatcher = computationDispatcher
        )
        scope = TestScope()
    }

    @Test
    fun `when user is not onboarded then should return not onboarded state`() = scope.runTest {
        every { getOnboardingState.invoke() } returns flowOf(OnboardingState.NotOnboarded)
        viewModel = DogListViewModel(
            getOnboardingState = getOnboardingState,
            dogsRepo = dogsRepo,
            savedStateHandle = SavedStateHandle(),
            ioDispatcher = ioDispatcher,
            computationDispatcher = computationDispatcher
        )

        viewModel.onboardingState.test {
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when user is onboarded then should return onboarded state`() = scope.runTest {
        viewModel.onboardingState.test {
            assertEquals(OnboardingState.Onboarded, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `initial state of viewmodel consists of loading and no dogs`() = scope.runTest {
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when no dogs exist and fetch dogs returns no dogs then loading is false and no dogs will be returned`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when fetch dogs returns dogs, then loading is false and dogs will be returned`() = scope.runTest {
        val history = viewModel.createStateHistory()
        dogsDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = true,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when fetch dogs returns an error then no dogs will be returned`() = scope.runTest {
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(DogListEvent.FetchDogs)
        (dogsRepo as FakeDogsRepo).forceError().also { advanceUntilIdle() }


        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when fetch dogs returns an error but there exists dogs then cached dogs will be emitted`() = scope.runTest {
        val history = viewModel.createStateHistory()
        dogsDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }

        (dogsRepo as FakeDogsRepo).forceError().also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = true,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when fetch dogs returns an error but there exists dogs in localState then cached dogs will be emitted`() = scope.runTest {
        // start collecting to update local state
        viewModel.createStateHistory()
        dogsDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }
        (dogsRepo as FakeDogsRepo).forceError().also { advanceUntilIdle() }


        // start collecting again, new subscribers will use cached dogs from local state
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when navigateEvent Add Dog is called, it is emitted as state`() = scope.runTest {
        val history = viewModel.createStateHistory()
        // call fetch dogs to clear loading state
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.AddDog).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = NavigateEvent.AddDog
            )
        )
    }

    @Test
    fun `when navigated to Add Dog, then navigateEvent is reset to null`() = scope.runTest {
        val history = viewModel.createStateHistory()
        // call fetch dogs to clear loading state
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.AddDog).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.OnNavigated).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = NavigateEvent.AddDog
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when navigated to Add Dog and dog is added then navigateEvent is reset to null and dog is emitted`() = scope.runTest {
        val history = viewModel.createStateHistory()
        // call fetch dogs to clear loading state
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.AddDog).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.OnNavigated).also { advanceUntilIdle() }
        dogsDataSource.addDogs(DOG_THREE).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = NavigateEvent.AddDog
            ),
            DogListState(
                isLoading = false,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_THREE),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when navigate to dog 2 is called then navigate to dog detail is emitted as state`() = scope.runTest {
        val history = viewModel.createStateHistory()
        dogsDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE).also { advanceUntilIdle() }
        // call fetch dogs to clear loading state
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.DogDetails(2)).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = true,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = NavigateEvent.DogDetails(2)
            )
        )
    }

    @Test
    fun `when navigated to dog 2 then navigate event is reset as null`() = scope.runTest {
        val history = viewModel.createStateHistory()
        dogsDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE).also { advanceUntilIdle() }
        // call fetch dogs to clear loading state
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.DogDetails(2)).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.OnNavigated).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = true,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = NavigateEvent.DogDetails(2)
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when dogs exist in repo then it loading state is emitted initially and dogs`() = scope.runTest {
        dogsDataSource = FakeDogsDataSource(listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        dogsRepo = FakeDogsRepo(
            dogsDataSource,
            isLoading = false
        )
        viewModel = DogListViewModel(
            getOnboardingState = getOnboardingState,
            dogsRepo = dogsRepo,
            savedStateHandle = SavedStateHandle(),
            ioDispatcher = ioDispatcher,
            computationDispatcher = computationDispatcher
        )
        val history = viewModel.createStateHistory()
        advanceUntilIdle()

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            )
        )

    }

    @Test
    fun `when dogs exist in repo and fetch dogs is called, state is not emitted after fetch dogs`() = scope.runTest {
        dogsDataSource = FakeDogsDataSource(listOf(DOG_ONE, DOG_TWO, DOG_THREE))
        dogsRepo = FakeDogsRepo(
            dogsDataSource,
            isLoading = false
        )
        viewModel = DogListViewModel(
            getOnboardingState = getOnboardingState,
            dogsRepo = dogsRepo,
            savedStateHandle = SavedStateHandle(),
            ioDispatcher = ioDispatcher,
            computationDispatcher = computationDispatcher
        )
        val history = viewModel.createStateHistory()
        advanceUntilIdle()
        viewModel.handleEvent(DogListEvent.FetchDogs).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            )
        )
    }




    
    // test event delete dog and see if dog is deleted
    // test weight format and date format



    private fun DogListViewModel.createStateHistory(): List<DogListState> {
        val history = mutableListOf<DogListState>()
        scope.backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            dogListState.toCollection(history)
        }
        return history
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
