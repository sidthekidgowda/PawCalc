package com.sidgowda.pawcalc

import com.sidgowda.pawcalc.data.modules.OnboardingDataModule
import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [OnboardingDataModule::class]
)
object TestOnboardingDataModule {

    @Singleton
    @Provides
    fun providesOnboardingRepo(
        onboardingDataSource: OnboardingDataSource
    ): OnboardingRepo {
        return OnboardingRepoImpl(onboardingDataSource)
    }

    @Singleton
    @Provides
    fun providesOnboardingDataSource(
    ): OnboardingDataSource {
        return FakeOnboardingDataSource
    }
}
