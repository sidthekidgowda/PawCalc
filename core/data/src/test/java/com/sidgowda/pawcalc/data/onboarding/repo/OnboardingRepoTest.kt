package com.sidgowda.pawcalc.data.onboarding.repo

import app.cash.turbine.test
import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingRepoTest {
    private object FakeOnboardingDataSource : OnboardingDataSource {

        var mutableSharedFlow = MutableSharedFlow<OnboardingState>(replay = 1)

        override val onboardingState: Flow<OnboardingState>
            get() = mutableSharedFlow.asSharedFlow()

        override suspend fun setUserOnboarded() {
            mutableSharedFlow.emit(OnboardingState.Onboarded)
        }
    }

    private lateinit var onboardingRepo: OnboardingRepo

    @Before
    fun setup() {
        onboardingRepo = OnboardingRepoImpl(FakeOnboardingDataSource)
        FakeOnboardingDataSource.mutableSharedFlow = MutableSharedFlow(replay = 1)
    }
    @Test
    fun `when not onboarded, then should return NotOnboarded`() = runTest {
        onboardingRepo.onboardingState.test {
            FakeOnboardingDataSource.mutableSharedFlow.emit(OnboardingState.NotOnboarded)
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
        }
    }

    @Test
    fun `when new collector collects again, then should return NotOnboarded`() = runTest {
        onboardingRepo.onboardingState.test {
            FakeOnboardingDataSource.mutableSharedFlow.emit(OnboardingState.NotOnboarded)
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
        }
        onboardingRepo.onboardingState.test {
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
        }
    }

    @Test
    fun `when OnboardingRepo setsUserOnboarded, then should return Onboarded`() = runTest {
        onboardingRepo.onboardingState.test {
            FakeOnboardingDataSource.setUserOnboarded()
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
    }

    @Test
    fun `given Onboarding returns NotOnboarded when setUserOnboarded, then should return Onboarded`() = runTest {
        onboardingRepo.onboardingState.test {
            FakeOnboardingDataSource.mutableSharedFlow.emit(OnboardingState.NotOnboarded)
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
            FakeOnboardingDataSource.setUserOnboarded()
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
    }

    @Test
    fun `given Onboarded, when newCollector subscribes, then should return Onboarded`() = runTest {
        onboardingRepo.onboardingState.test {
            FakeOnboardingDataSource.setUserOnboarded()
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
        onboardingRepo.onboardingState.test {
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
    }
}
