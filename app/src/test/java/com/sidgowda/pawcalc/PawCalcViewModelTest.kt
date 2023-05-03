package com.sidgowda.pawcalc

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.domain.onboarding.GetOnboardingStateUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PawCalcViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var pawCalcViewModel: PawCalcViewModel
    private lateinit var onboardingStateUseCase: GetOnboardingStateUseCase
    private lateinit var getSettingsUseCase: GetSettingsUseCase

    @Before
    fun setup() {
        onboardingStateUseCase = mockk()
        getSettingsUseCase = mockk()
        testDispatcher = StandardTestDispatcher()
        scope = TestScope()
    }

    @Test
    fun `initial state should be Loading`() {
        every { onboardingStateUseCase.invoke() } returns emptyFlow()
        every { getSettingsUseCase.invoke() } returns emptyFlow()
        pawCalcViewModel =
            PawCalcViewModel(onboardingStateUseCase, getSettingsUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading
        )
    }

    @Test
    fun `when onboarding is not onboarded then state should contain loading and not onboarded`() {
        every { onboardingStateUseCase.invoke() } returns flowOf(OnboardingState.NotOnboarded)
        every { getSettingsUseCase.invoke() } returns flowOf(DEFAULT_SETTINGS)
        pawCalcViewModel =
            PawCalcViewModel(onboardingStateUseCase, getSettingsUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.NotOnboarded,
                settings = DEFAULT_SETTINGS
            )
        )
    }

    @Test
    fun `when onboarding is onboarded then state should contain loading and onboarded`() {
        every { onboardingStateUseCase.invoke() } returns flowOf(OnboardingState.Onboarded)
        every { getSettingsUseCase.invoke() } returns flowOf(DEFAULT_SETTINGS)
        pawCalcViewModel =
            PawCalcViewModel(onboardingStateUseCase, getSettingsUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.Onboarded,
                settings = DEFAULT_SETTINGS
            )
        )
    }

    @Test
    fun `when onboardingUseCase throws error then state should contain loading and not onboarded`() {
        every { onboardingStateUseCase.invoke() } returns flow {
            throw Exception()
        }
        every { getSettingsUseCase.invoke() } returns flowOf(DEFAULT_SETTINGS)
        pawCalcViewModel =
            PawCalcViewModel(onboardingStateUseCase, getSettingsUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.NotOnboarded,
                settings = DEFAULT_SETTINGS
            )
        )
    }

    @Test
    fun `when settings flow throws error, then state should emit default settings`() {
        every { onboardingStateUseCase.invoke() } returns flowOf(OnboardingState.Onboarded)
        every { getSettingsUseCase.invoke() } returns flow {
            throw Exception()
        }
        pawCalcViewModel =
            PawCalcViewModel(onboardingStateUseCase, getSettingsUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.Onboarded,
                settings = DEFAULT_SETTINGS
            )
        )
    }

    @Test
    fun `when settings has value in disk, it shoudl be emitted first over default settings`() {
        every { onboardingStateUseCase.invoke() } returns flowOf(OnboardingState.Onboarded)
        every { getSettingsUseCase.invoke() } returns flowOf(DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL))

        pawCalcViewModel =
            PawCalcViewModel(onboardingStateUseCase, getSettingsUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.Onboarded,
                settings = DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL)
            )
        )
    }

    @Test
    fun `when settings is updated, then state should emit all updated settings`() = scope.runTest {
        every { onboardingStateUseCase.invoke() } returns flowOf(OnboardingState.Onboarded)
        every { getSettingsUseCase.invoke() } returns flow {
            emit(DEFAULT_SETTINGS)
            delay(5_000)
            emit(
                DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS)
            )
            delay(5_000)
            emit(
                DEFAULT_SETTINGS.copy(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL
                )
            )
            delay(1_000)
            emit(
                DEFAULT_SETTINGS.copy(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.DARK)
            )
        }
        pawCalcViewModel =
            PawCalcViewModel(onboardingStateUseCase, getSettingsUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.Onboarded,
                settings = DEFAULT_SETTINGS
            ),
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.Onboarded,
                settings = DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS)
            ),
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.Onboarded,
                settings = DEFAULT_SETTINGS.copy(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL
                )
            ),
            PawCalcActivityState.Initialized(
                onboardingState = OnboardingState.Onboarded,
                settings = DEFAULT_SETTINGS.copy(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.DARK)
            )
        )
    }

    private fun PawCalcViewModel.createStateHistory(): List<PawCalcActivityState> {
        val history = mutableListOf<PawCalcActivityState>()
        scope.backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            uiState.toCollection(history)
        }
        return history
    }

    private companion object {
        private val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    }
}
