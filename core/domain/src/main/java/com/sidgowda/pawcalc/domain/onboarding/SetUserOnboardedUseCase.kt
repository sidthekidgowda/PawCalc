package com.sidgowda.pawcalc.domain.onboarding

import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import javax.inject.Inject

class SetUserOnboardedUseCase @Inject constructor(
    private val onboardingRepo: OnboardingRepo
) {
    suspend operator fun invoke() {
        onboardingRepo.setUserOnboarded()
    }
}
