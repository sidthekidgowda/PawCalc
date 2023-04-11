package com.sidgowda.pawcalc.data.onboarding.di

import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSourceImpl
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingDataModule {

    @Singleton
    @Provides
    fun providesOnboardingRepo(
        onboardingDataSource: OnboardingDataSourceImpl
    ): OnboardingRepo {
        return OnboardingRepoImpl(onboardingDataSource)
    }

    @Singleton
    @Provides
    fun providesOnboardingDataSource(): OnboardingDataSource {
        return OnboardingDataSourceImpl()
    }

}
