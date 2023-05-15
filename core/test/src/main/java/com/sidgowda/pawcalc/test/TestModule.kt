package com.sidgowda.pawcalc.test

import com.sidgowda.pawcalc.data.modules.DispatchersModule
import com.sidgowda.pawcalc.data.modules.SettingsModule
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import com.sidgowda.pawcalc.test.fakes.FakeSettingsDataSource
import com.sidgowda.pawcalc.test.fakes.FakeSettingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

object TestModule {

    @Module
    @TestInstallIn(
        components = [SingletonComponent::class],
        replaces = [SettingsModule::class],
    )
    object TestSettingsModule {

        @Provides
        @Singleton
        fun providesSettingsRepo(): SettingsRepo {
            return FakeSettingsRepo()
        }

        @Provides
        @Singleton
        fun providesFakeSettingsDataSource(): SettingsDataSource {
            return FakeSettingsDataSource()
        }
    }


    @Module
    @TestInstallIn(
        components = [SingletonComponent::class],
        replaces = [DispatchersModule::class]
    )
    object TestDispatchersModule {

        @Provides
        @Singleton
        @Named("io")
        fun providesIoDispatcher(): CoroutineDispatcher {
            return IdlingResourceCoroutineDispatcher(Dispatchers.IO)
        }

        @Provides
        @Singleton
        @Named("computation")
        fun providesComputationDispatcher(): CoroutineDispatcher {
            return IdlingResourceCoroutineDispatcher(Dispatchers.Default)
        }

        // convenience functions to avoid casting in tests
        @Provides
        @Singleton
        @Named("io")
        fun providesTestIOIdlingDispatcher(
            @Named("io") dispatcher: CoroutineDispatcher
        ): IdlingResourceCoroutineDispatcher {
            return dispatcher as IdlingResourceCoroutineDispatcher
        }

        @Provides
        @Singleton
        @Named("computation")
        fun providesTestComputationIdlingDispatcher(
            @Named("computation") dispatcher: CoroutineDispatcher
        ): IdlingResourceCoroutineDispatcher {
            return dispatcher as IdlingResourceCoroutineDispatcher
        }
    }
}
