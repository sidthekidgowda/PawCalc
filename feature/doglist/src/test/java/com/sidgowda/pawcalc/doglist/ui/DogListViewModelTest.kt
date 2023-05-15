package com.sidgowda.pawcalc.doglist.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.formattedToTwoDecimals
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.doglist.FakeDogsDataSource
import com.sidgowda.pawcalc.doglist.FakeDogsRepo
import com.sidgowda.pawcalc.doglist.model.DogListEvent
import com.sidgowda.pawcalc.doglist.model.DogListState
import com.sidgowda.pawcalc.doglist.model.NavigateEvent
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
        advanceUntilIdle()

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

        (dogsRepo as FakeDogsRepo).forceError().also { advanceUntilIdle() }

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
    fun `when fetch dogs returns an error but there exists dogs in localState then cached dogs will be emitted`() = scope.runTest {
        // start collecting to update local state
        viewModel.createStateHistory()
        dogsDataSource.addDogs(DOG_ONE, DOG_TWO, DOG_THREE).also { advanceUntilIdle() }
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
                navigateEvent = NavigateEvent.AddDog
            )
        )
    }

    @Test
    fun `when navigated to Add Dog, then navigateEvent is reset to null`() = scope.runTest {
        val history = viewModel.createStateHistory()
        // call fetch dogs to clear loading state
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
        viewModel.handleEvent(DogListEvent.DogDetails(2)).also { advanceUntilIdle() }

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
        viewModel.handleEvent(DogListEvent.DogDetails(2)).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.OnNavigated).also { advanceUntilIdle() }

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
    fun `when dogs exist in repo then loading state is emitted initially and dogs`() = scope.runTest {
        addDogsBeforeViewModelIsCreated()
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
        addDogsBeforeViewModelIsCreated()
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
    fun `when delete dog 1 is called then dog one should not exist`() = scope.runTest {
        addDogsBeforeViewModelIsCreated()
        val history = viewModel.createStateHistory()
        advanceUntilIdle()

        viewModel.handleEvent(DogListEvent.DeleteDog(DOG_ONE)).also { advanceUntilIdle() }

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
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_TWO, DOG_THREE),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when delete dog 2 is called then dog two should not exist`() = scope.runTest {
        addDogsBeforeViewModelIsCreated()
        val history = viewModel.createStateHistory()
        advanceUntilIdle()

        viewModel.handleEvent(DogListEvent.DeleteDog(DOG_TWO)).also { advanceUntilIdle() }

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
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_THREE),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when delete dog 3 is called then dog three should not exist`() = scope.runTest {
        addDogsBeforeViewModelIsCreated()
        val history = viewModel.createStateHistory()
        advanceUntilIdle()

        viewModel.handleEvent(DogListEvent.DeleteDog(DOG_THREE)).also { advanceUntilIdle() }

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
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when all dogs are deleted then no dogs should exist`() = scope.runTest {
        addDogsBeforeViewModelIsCreated()
        val history = viewModel.createStateHistory()
        advanceUntilIdle()

        viewModel.handleEvent(DogListEvent.DeleteDog(DOG_THREE)).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.DeleteDog(DOG_TWO)).also { advanceUntilIdle() }
        viewModel.handleEvent(DogListEvent.DeleteDog(DOG_ONE)).also { advanceUntilIdle() }

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
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE),
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
    fun `when weight format is changed to kilograms, all dogs weight should be in kilograms`() = scope.runTest {
        addDogsBeforeViewModelIsCreated()
        val history = viewModel.createStateHistory()
        advanceUntilIdle()
        dogsDataSource.updateDogs(
            DOG_ONE.copy(
                weight = DOG_ONE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                weightFormat = WeightFormat.KILOGRAMS
            ),
            DOG_TWO.copy(
                weight = DOG_TWO.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                weightFormat = WeightFormat.KILOGRAMS
            ),
            DOG_THREE.copy(
                weight = DOG_THREE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                weightFormat = WeightFormat.KILOGRAMS
            )
        ).also { advanceUntilIdle() }

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
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE.copy(
                        weight = DOG_ONE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                        weightFormat = WeightFormat.KILOGRAMS
                    ),
                    DOG_TWO.copy(
                        weight = DOG_TWO.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                        weightFormat = WeightFormat.KILOGRAMS
                    ),
                    DOG_THREE.copy(
                        weight = DOG_THREE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                        weightFormat = WeightFormat.KILOGRAMS
                    )
                ),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when weight format is in kilograms and changed back to lbs, all dogs weight should be in lbs`() = scope.runTest {
        addDogsBeforeViewModelIsCreated(
            listOf(
                DOG_ONE.copy(
                    weight = DOG_ONE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                    weightFormat = WeightFormat.KILOGRAMS
                ),
                DOG_TWO.copy(
                    weight = DOG_TWO.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                    weightFormat = WeightFormat.KILOGRAMS
                ),
                DOG_THREE.copy(
                    weight = DOG_THREE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                    weightFormat = WeightFormat.KILOGRAMS
                )
            )
        )
        val history = viewModel.createStateHistory()
        advanceUntilIdle()
        dogsDataSource.updateDogs(
            DOG_ONE.copy(
                weight = DOG_ONE.weight,
                weightFormat = WeightFormat.POUNDS
            ),
            DOG_TWO.copy(
                weight = DOG_TWO.weight,
                weightFormat = WeightFormat.POUNDS
            ),
            DOG_THREE.copy(
                weight = DOG_THREE.weight,
                weightFormat = WeightFormat.POUNDS
            )
        ).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE.copy(
                        weight = DOG_ONE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                        weightFormat = WeightFormat.KILOGRAMS
                    ),
                    DOG_TWO.copy(
                        weight = DOG_TWO.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                        weightFormat = WeightFormat.KILOGRAMS
                    ),
                    DOG_THREE.copy(
                        weight = DOG_THREE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
                        weightFormat = WeightFormat.KILOGRAMS
                    )
                ),
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
    fun `when date is changed to international, all dates should be in international format`() = scope.runTest {
        addDogsBeforeViewModelIsCreated()
        val history = viewModel.createStateHistory()
        advanceUntilIdle()
        dogsDataSource.updateDogs(
            DOG_ONE.copy(
                birthDate = "22/12/2021",
                dateFormat = DateFormat.INTERNATIONAL
            ),
            DOG_TWO.copy(
                birthDate = "12/12/2021",
                dateFormat = DateFormat.INTERNATIONAL
            ),
            DOG_THREE.copy(
                birthDate = "12/12/2021",
                dateFormat = DateFormat.INTERNATIONAL
            )
        ).also { advanceUntilIdle() }

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
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE.copy(
                        birthDate = "22/12/2021",
                        dateFormat = DateFormat.INTERNATIONAL
                    ),
                    DOG_TWO.copy(
                        birthDate = "12/12/2021",
                        dateFormat = DateFormat.INTERNATIONAL
                    ),
                    DOG_THREE.copy(
                        birthDate = "12/12/2021",
                        dateFormat = DateFormat.INTERNATIONAL
                    )
                ),
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when date format is international and changed to american, all birthdates should be in american`() = scope.runTest {
        addDogsBeforeViewModelIsCreated(
            listOf(
                DOG_ONE.copy(
                    birthDate = "22/12/2021",
                    dateFormat = DateFormat.INTERNATIONAL
                ),
                DOG_TWO.copy(
                    birthDate = "12/12/2021",
                    dateFormat = DateFormat.INTERNATIONAL
                ),
                DOG_THREE.copy(
                    birthDate = "12/12/2021",
                    dateFormat = DateFormat.INTERNATIONAL
                )
            )
        )
        val history = viewModel.createStateHistory()
        advanceUntilIdle()
        dogsDataSource.updateDogs(
            DOG_ONE,
            DOG_TWO,
            DOG_THREE
        ).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DogListState(
                isLoading = true,
                dogs = emptyList(),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(
                    DOG_ONE.copy(
                        birthDate = "22/12/2021",
                        dateFormat = DateFormat.INTERNATIONAL
                    ),
                    DOG_TWO.copy(
                        birthDate = "12/12/2021",
                        dateFormat = DateFormat.INTERNATIONAL
                    ),
                    DOG_THREE.copy(
                        birthDate = "12/12/2021",
                        dateFormat = DateFormat.INTERNATIONAL
                    )
                ),
                navigateEvent = null
            ),
            DogListState(
                isLoading = false,
                dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE),
                navigateEvent = null
            )
        )
    }

    private fun addDogsBeforeViewModelIsCreated(dogs: List<Dog> = listOf(DOG_ONE, DOG_TWO, DOG_THREE)) {
        dogsDataSource = FakeDogsDataSource(dogs)
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
    }

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
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
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
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
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
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
    }
}
