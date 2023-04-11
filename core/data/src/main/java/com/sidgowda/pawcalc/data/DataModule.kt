package com.sidgowda.pawcalc.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesOnboardingRepo(
        onboardingDataSource: OnboardingDataSource
    ): OnboardingRepo {
        return OnboardingRepoImpl(onboardingDataSource)
    }

    @Singleton
    @Provides
    fun providesOnboardingDataSource(): OnboardingDataSource {
        return OnboardingDataSource()
    }
}
