package com.sidgowda.pawcalc.domain

import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import javax.inject.Inject

class SetUserOnboardedUseCase @Inject constructor(
    private val onboardingRepo: OnboardingRepo
) {
    operator suspend fun invoke() {
        onboardingRepo.setUserOnboarded()
    }
}
