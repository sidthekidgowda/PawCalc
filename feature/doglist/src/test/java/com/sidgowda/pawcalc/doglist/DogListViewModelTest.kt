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
    fun `when no dogs exist and viewmodel is subscribed, then state will consist of loading and no dogs`() = scope.runTest {
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
        dogsDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE)
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


    // test event fetchDogs - error
    // test event fetchDogs - dogs
    // test event fetchDogs - error use cache
    // test event navigate to add Dog and local state
    // test event add dog and see if dog is added
    // test event navigate to dog details and local state
    // test event delete dog and see if dog is deleted



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
