package com.sidgowda.pawcalc.dogdetails

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.dogdetails.model.DogDetailsState
import com.sidgowda.pawcalc.dogdetails.ui.DogDetailsViewModel
import com.sidgowda.pawcalc.domain.dogs.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class DogDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var computationDispatcher: CoroutineDispatcher
    private lateinit var ioDispatcher: CoroutineDispatcher
    private lateinit var getDogForIdUseCase: GetDogForIdUseCase
    private lateinit var settingsUseCase: GetSettingsUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DogDetailsViewModel
    private lateinit var settingsFlow: MutableStateFlow<Settings>
    private val dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE)

    @Before
    fun setup() {
        ioDispatcher = StandardTestDispatcher()
        computationDispatcher = StandardTestDispatcher()
        getDogForIdUseCase = mockk()
        settingsUseCase = mockk()
        savedStateHandle = mockk(relaxed = true)
        scope = TestScope(computationDispatcher)
        settingsFlow = MutableStateFlow(DEFAULT_SETTINGS)
        every { settingsUseCase.invoke() } returns settingsFlow
        coEvery { getDogForIdUseCase.invoke(any()) } answers {
            val id = firstArg<Int>()
            if (id > 3) {
                throw IllegalArgumentException("There are only 3 items in list")
            } else {
                flowOf(dogs[id-1])
            }
        }
    }

    @Test
    fun `when fetchDog is called for id 1 then dog 1 must be emitted as state`()  = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

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
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

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
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

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
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE
        )
    }
    // test bad id

    private fun initializeViewModel() {
        viewModel = DogDetailsViewModel(
            getDogForIdUseCase = getDogForIdUseCase,
            settingsUseCase = settingsUseCase,
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
            birthDate = "7/30/2019",
            weight = 75.0,
            dogYears = "7/30/2019".toDogYears(),
            humanYears = "7/30/2019".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        private val DOG_TWO = Dog(
            id = 2,
            name = "Tucker",
            profilePic = Uri.parse("http://image2"),
            birthDate = "4/15/2019",
            weight = 85.0,
            dogYears = "4/15/2019".toDogYears(),
            humanYears = "4/15/2019".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        private val DOG_THREE = Dog(
            id = 3,
            name = "Todd",
            profilePic = Uri.parse("http://image2"),
            birthDate = "12/1/2022",
            weight = 65.0,
            dogYears = "12/1/2022".toDogYears(),
            humanYears = "12/1/2022".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        private val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
        private val INITIAL_STATE = DogDetailsState()
    }


}
