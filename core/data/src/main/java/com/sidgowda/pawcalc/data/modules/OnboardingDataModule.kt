package com.sidgowda.pawcalc.data.modules

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataStore
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
        onboardingDataSource: OnboardingDataSource
    ): OnboardingRepo {
        return OnboardingRepoImpl(onboardingDataSource)
    }

    @Singleton
    @Provides
    fun providesOnboardingDataSource(
        dataStore: DataStore<Preferences>
    ): OnboardingDataSource {
        return OnboardingDataStore(dataStore)
    }

}
