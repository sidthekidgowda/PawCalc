package com.sidgowda.pawcalc.domain.onboarding

import app.cash.turbine.test
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetOnboardingStateUseCaseTest {

    private object FakeOnboardingRepo : OnboardingRepo {

        var mutableSharedFlow = MutableSharedFlow<OnboardingState>(replay = 1)

        override val onboardingState: Flow<OnboardingState>
            get() = mutableSharedFlow.asSharedFlow()

        override suspend fun setUserOnboarded() {
            mutableSharedFlow.emit(OnboardingState.Onboarded)
        }
    }

    private lateinit var getOnboardingStateUseCase: GetOnboardingStateUseCase

    @Before
    fun setup() {
        getOnboardingStateUseCase = GetOnboardingStateUseCase(FakeOnboardingRepo)
        FakeOnboardingRepo.mutableSharedFlow = MutableSharedFlow(replay = 1)
    }

    @Test
    fun `when not onboarded, then should return NotOnboarded`() = runTest {
        getOnboardingStateUseCase.invoke().test {
            FakeOnboardingRepo.mutableSharedFlow.emit(OnboardingState.NotOnboarded)
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
        }
    }

    @Test
    fun `when new collector collects again, then should return NotOnboarded`() = runTest {
        getOnboardingStateUseCase.invoke().test {
            FakeOnboardingRepo.mutableSharedFlow.emit(OnboardingState.NotOnboarded)
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
        }
        getOnboardingStateUseCase.invoke().test {
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
        }
    }

    @Test
    fun `when OnboardingRepo setsUserOnboarded, then should return Onboarded`() = runTest {
        getOnboardingStateUseCase.invoke().test {
            FakeOnboardingRepo.setUserOnboarded()
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
    }

    @Test
    fun `given Onboarding returns NotOnboarded when setUserOnboarded, then should return Onboarded`() = runTest {
        getOnboardingStateUseCase.invoke().test {
            FakeOnboardingRepo.mutableSharedFlow.emit(OnboardingState.NotOnboarded)
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
            FakeOnboardingRepo.setUserOnboarded()
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
    }

    @Test
    fun `given Onboarded, when newCollector subscribes, then should return Onboarded`() = runTest {
        getOnboardingStateUseCase.invoke().test {
            FakeOnboardingRepo.setUserOnboarded()
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
        getOnboardingStateUseCase.invoke().test {
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
    }
}
