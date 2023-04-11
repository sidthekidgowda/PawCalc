package com.sidgowda.pawcalc.domain

import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsUserOnboardedUseCase @Inject constructor(
    private val onboardingRepo: OnboardingRepo
) {
    operator fun invoke(): Flow<OnboardingState> {
        return onboardingRepo.hasUserOnboarded()
    }
}
