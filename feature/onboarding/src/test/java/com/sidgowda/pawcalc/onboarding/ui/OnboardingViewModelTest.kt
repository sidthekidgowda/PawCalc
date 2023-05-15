package com.sidgowda.pawcalc.onboarding.ui

import com.sidgowda.pawcalc.domain.onboarding.SetUserOnboardedUseCase
import com.sidgowda.pawcalc.onboarding.ui.OnboardingViewModel
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userOnboardedUseCase: SetUserOnboardedUseCase
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setup() {
        userOnboardedUseCase = mockk()
        viewModel = OnboardingViewModel(userOnboardedUseCase)
        coEvery { userOnboardedUseCase.invoke() } just runs
    }

    @Test
    fun `verify user has not onboarded`() {
        coVerify(exactly = 0) { userOnboardedUseCase.invoke() }
    }

    @Test
    fun `verify user onboarded`() = runTest {
        viewModel.setUserOnboarded()
        coVerify(exactly = 1) { userOnboardedUseCase.invoke() }
    }
}
