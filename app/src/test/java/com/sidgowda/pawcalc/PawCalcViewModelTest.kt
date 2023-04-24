package com.sidgowda.pawcalc

import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.domain.GetOnboardingStateUseCase
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Before
    fun setup() {
        onboardingStateUseCase = mockk()
        testDispatcher = StandardTestDispatcher()
        scope = TestScope()

    }

    @Test
    fun `initial state should be Loading`() {
        every { onboardingStateUseCase.invoke() } returns emptyFlow()
        pawCalcViewModel = PawCalcViewModel(onboardingStateUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading
        )
    }

    @Test
    fun `when onboarding is not onboarded then state should contain loading and not onboarded`() {
        every { onboardingStateUseCase.invoke() } returns flowOf(OnboardingState.NotOnboarded)
        pawCalcViewModel = PawCalcViewModel(onboardingStateUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(onboardingState = OnboardingState.NotOnboarded)
        )
    }

    @Test
    fun `when onboarding is onboarded then state should contain loading and onboarded`() {
        every { onboardingStateUseCase.invoke() } returns flowOf(OnboardingState.Onboarded)
        pawCalcViewModel = PawCalcViewModel(onboardingStateUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(onboardingState = OnboardingState.Onboarded)
        )
    }

    @Test
    fun `when onboardingUseCase throws error then state should contain loading and not onboarded`() {
        every { onboardingStateUseCase.invoke() } returns flow {
            throw Exception()
        }
        pawCalcViewModel = PawCalcViewModel(onboardingStateUseCase, testDispatcher)
        val history = pawCalcViewModel.createStateHistory()

        scope.advanceUntilIdle()

        history shouldContainExactly listOf(
            PawCalcActivityState.Loading,
            PawCalcActivityState.Initialized(onboardingState = OnboardingState.NotOnboarded)
        )
    }

    private fun PawCalcViewModel.createStateHistory(): List<PawCalcActivityState> {
        val history = mutableListOf<PawCalcActivityState>()
        scope.backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            uiState.toCollection(history)
        }
        return history
    }
}
