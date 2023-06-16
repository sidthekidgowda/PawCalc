package com.sidgowda.pawcalc.dogdetails.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.dogdetails.model.DogDetailsEvent
import com.sidgowda.pawcalc.dogdetails.model.DogDetailsState
import com.sidgowda.pawcalc.dogdetails.model.NavigateEvent
import com.sidgowda.pawcalc.domain.dogs.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.dogs.UpdateDogUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DogDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var computationDispatcher: CoroutineDispatcher
    private lateinit var ioDispatcher: CoroutineDispatcher
    private lateinit var getDogForIdUseCase: GetDogForIdUseCase
    private lateinit var updateDogUseCase: UpdateDogUseCase
    private lateinit var settingsUseCase: GetSettingsUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DogDetailsViewModel
    private lateinit var settingsFlow: MutableStateFlow<Settings>
    private lateinit var dogFlow: MutableSharedFlow<Dog>

    @Before
    fun setup() {
        ioDispatcher = mainDispatcherRule.testDispatcher
        computationDispatcher = mainDispatcherRule.testDispatcher
        getDogForIdUseCase = mockk()
        updateDogUseCase = mockk()
        settingsUseCase = mockk()
        savedStateHandle = mockk(relaxed = true)
        scope = TestScope(mainDispatcherRule.testDispatcher)
        settingsFlow = MutableStateFlow(DEFAULT_SETTINGS)
        dogFlow = MutableSharedFlow()
        every { settingsUseCase.invoke() } returns settingsFlow
        every { getDogForIdUseCase.invoke(any()) } returns dogFlow
        coEvery { updateDogUseCase.invoke(any()) } just runs
    }

    @Test
    fun `when fetchDog is called for id 1 then dog 1 must be emitted as state`()  = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_ONE)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_ONE
            )
        )
    }

    @Test
    fun `when fetchDog is called for id 2 then dog 2 must be emitted as state`()  = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        dogFlow.emit(DOG_TWO)
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_TWO)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_TWO
            )
        )
    }

    @Test
    fun `when fetchDog is called for id 3 then dog 3 must be emitted as state`()  = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_THREE)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_THREE
            )
        )
    }

    @Test
    fun `when fetchDog is called for id that does not exist then no dog should be emitted`()  = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 4
        initializeViewModel()
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            INITIAL_STATE
        )
    }

    @Test
    fun `when settings date format is changed, then current dog date should be updated`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_THREE)
        settingsFlow.emit(DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_THREE
            ),
            DogDetailsState(
                dog = DOG_THREE.copy(
                    dateFormat = DateFormat.INTERNATIONAL
                )
            )
        )
    }

    @Test
    fun `when settings weight format is changed, then current dog weight should be updated`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_TWO)
        settingsFlow.emit(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_TWO
            ),
            DogDetailsState(
                dog = DOG_TWO.copy(
                    weightFormat = WeightFormat.KILOGRAMS
                )
            )
        )
    }

    @Test
    fun `when dog 2 is edited and updated, updated dog should be emitted as new state`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        dogFlow.emit(DOG_TWO).also { advanceUntilIdle() }
        dogFlow.emit(DOG_TWO.copy(name = "updated_name")).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_TWO
            ),
            DogDetailsState(
                dog = DOG_TWO.copy(
                    name = "updated_name"
                )
            )
        )
    }

    @Test
    fun `when dog 1 is edited and updated, updated dog should be emitted as new state`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_ONE)
        dogFlow.emit(DOG_ONE.copy(weightInLb = 58.0))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_ONE
            ),
            DogDetailsState(
                dog = DOG_ONE.copy(
                    weightInLb = 58.0
                )
            )
        )
    }

    @Test
    fun `when dog 3 is edited and updated, updated dog should be emitted as new state`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_THREE)
        dogFlow.emit(
            DOG_THREE.copy(
            birthDateAmerican = "7/30/2019"
        ))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_THREE
            ),
            DogDetailsState(
                dog = DOG_THREE.copy(
                    birthDateAmerican = "7/30/2019"
                )
            )
        )
    }

    @Test
    fun `when event edit dog is called for dog 1, navigate event edit dog is emitted as state`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_ONE)
        viewModel.handleEvent(DogDetailsEvent.EditDog)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_ONE
            ),
            DogDetailsState(
                dog = DOG_ONE,
                navigateEvent = NavigateEvent.EditDog(1)
            )
        )
    }

    @Test
    fun `when event edit dog is called for dog 2, navigate event edit dog is emitted as state`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_TWO)
        viewModel.handleEvent(DogDetailsEvent.EditDog)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_TWO
            ),
            DogDetailsState(
                dog = DOG_TWO,
                navigateEvent = NavigateEvent.EditDog(2)
            )
        )
    }

    @Test
    fun `when event edit dog is called for dog 3, navigate event edit dog is emitted as state`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_THREE)
        viewModel.handleEvent(DogDetailsEvent.EditDog)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_THREE
            ),
            DogDetailsState(
                dog = DOG_THREE,
                navigateEvent = NavigateEvent.EditDog(3)
            )
        )
    }

    @Test
    fun `when OnNavigated is called for dog 1, then navigate event is reset to null`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_ONE)
        viewModel.handleEvent(DogDetailsEvent.EditDog)
        viewModel.handleEvent(DogDetailsEvent.OnNavigated)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_ONE
            ),
            DogDetailsState(
                dog = DOG_ONE,
                navigateEvent = NavigateEvent.EditDog(1)
            ),
            DogDetailsState(
                dog = DOG_ONE,
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when OnNavigated is called for dog 2, then navigate event is reset to null`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_TWO)
        viewModel.handleEvent(DogDetailsEvent.EditDog)
        viewModel.handleEvent(DogDetailsEvent.OnNavigated)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_TWO
            ),
            DogDetailsState(
                dog = DOG_TWO,
                navigateEvent = NavigateEvent.EditDog(2)
            ),
            DogDetailsState(
                dog = DOG_TWO,
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when OnNavigated is called for dog 3, then navigate event is reset to null`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_THREE)
        viewModel.handleEvent(DogDetailsEvent.EditDog)
        viewModel.handleEvent(DogDetailsEvent.OnNavigated)

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_THREE
            ),
            DogDetailsState(
                dog = DOG_THREE,
                navigateEvent = NavigateEvent.EditDog(3)
            ),
            DogDetailsState(
                dog = DOG_THREE,
                navigateEvent = null
            )
        )
    }

    @Test
    fun `when event OnAnimationFinished is called for Dog2, then Dog2 shouldAnimate will be updated to false`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_TWO)
        viewModel.handleEvent(DogDetailsEvent.OnAnimationFinished)
        dogFlow.emit(DOG_TWO.copy(shouldAnimate = false))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_TWO
            ),
            DogDetailsState(
                dog = DOG_TWO.copy(shouldAnimate = false),
            )
        )
    }

    @Test
    fun `when Dog 1 birthdate is changed after animation has finished, then shouldAnimate will be updated to true`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory()
        dogFlow.emit(DOG_ONE)
        viewModel.handleEvent(DogDetailsEvent.OnAnimationFinished)
        dogFlow.emit(DOG_ONE.copy(shouldAnimate = false))
        dogFlow.emit(
            DOG_ONE.copy(birthDateAmerican = "7/30/2022", shouldAnimate = true)
        )

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogDetailsState(
                dog = DOG_ONE
            ),
            DogDetailsState(
                dog = DOG_ONE.copy(shouldAnimate = false),
            ),
            DogDetailsState(
                dog = DOG_ONE.copy(shouldAnimate = true, birthDateAmerican = "7/30/2022")
            )
        )
    }

    private fun initializeViewModel() {
        viewModel = DogDetailsViewModel(
            getDogForIdUseCase = getDogForIdUseCase,
            settingsUseCase = settingsUseCase,
            updateDogUseCase = updateDogUseCase,
            savedStateHandle = savedStateHandle,
            computationDispatcher = computationDispatcher
        )
    }

    private fun DogDetailsViewModel.createStateHistory(): List<DogDetailsState> {
        val history = mutableListOf<DogDetailsState>()
        scope.backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            dogDetailsState.toCollection(history)
        }
        return history
    }

    private companion object {
        private val DOG_ONE = Dog(
            id = 1,
            name = "Mowgli",
            profilePic = Uri.parse("http://image1"),
            birthDateAmerican = "7/30/2019",
            birthDateInternational = "30/7/2019",
            weightInLb = 75.0,
            weightInKg = 75.0.toNewWeight(WeightFormat.KILOGRAMS),
            dogYears = "7/30/2019".toDogYears(),
            humanYears = "7/30/2019".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
        private val DOG_TWO = Dog(
            id = 2,
            name = "Tucker",
            profilePic = Uri.parse("http://image2"),
            birthDateAmerican = "4/15/2019",
            birthDateInternational = "15/4/2019",
            weightInLb = 85.0,
            weightInKg = 85.0.toNewWeight(WeightFormat.KILOGRAMS),
            dogYears = "4/15/2019".toDogYears(),
            humanYears = "4/15/2019".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
        private val DOG_THREE = Dog(
            id = 3,
            name = "Todd",
            profilePic = Uri.parse("http://image2"),
            birthDateAmerican = "12/1/2022",
            birthDateInternational = "1/12/2019",
            weightInLb = 65.0,
            weightInKg = 65.0.toNewWeight(WeightFormat.KILOGRAMS),
            dogYears = "12/1/2022".toDogYears(),
            humanYears = "12/1/2022".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
        private val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
        private val INITIAL_STATE = DogDetailsState()
    }
}
