package com.sidgowda.pawcalc.domain

import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import com.sidgowda.pawcalc.domain.dogs.SetUserOnboardedUseCase
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SetUserOnboardedUseCaseTest {

    private lateinit var onboardingRepo: OnboardingRepo
    private lateinit var userOnboardedUseCase: SetUserOnboardedUseCase

    @Before
    fun setup() {
        onboardingRepo = mockk()
        userOnboardedUseCase = SetUserOnboardedUseCase(onboardingRepo)
        coEvery { onboardingRepo.setUserOnboarded() } just runs
    }

    @Test
    fun `verify user has not onboarded`() = runTest {
        coVerify(exactly = 0) { onboardingRepo.setUserOnboarded() }
    }

    @Test
    fun `verify user onboarded`() = runTest {
        userOnboardedUseCase.invoke()
        coVerify(exactly = 1) { onboardingRepo.setUserOnboarded() }
    }
}
