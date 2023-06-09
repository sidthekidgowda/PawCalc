package com.sidgowda.pawcalc.test

import android.net.Uri
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepoImpl
import com.sidgowda.pawcalc.data.modules.DispatchersModule
import com.sidgowda.pawcalc.data.modules.DogsDataModule
import com.sidgowda.pawcalc.data.modules.SettingsModule
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import com.sidgowda.pawcalc.test.fakes.FakeDogsDataSource
import com.sidgowda.pawcalc.test.fakes.FakeSettingsDataSource
import com.sidgowda.pawcalc.test.fakes.FakeSettingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

object TestModule {

    @Module
    @TestInstallIn(
        components = [SingletonComponent::class],
        replaces = [DogsDataModule::class]
    )
    object TestDogsDataModule {

        @Named("memory")
        @Singleton
        @Provides
        fun providesMemoryDogsDataSource(
            settingsDataSource: SettingsDataSource,
            @Named("computationScope") scope: CoroutineScope
        ): DogsDataSource {
            return DogsMemoryDataSource(settingsDataSource, scope)
        }

        @Named("disk")
        @Singleton
        @Provides
        fun providesDiskDogsDataSource(): DogsDataSource {
            return FakeDogsDataSource(
                listOf(
                    Dog(
                        id = 1,
                        name = "Dog_1",
                        weight = 68.0,
                        profilePic = Uri.EMPTY,
                        birthDate = "12/1/2021",
                        dogYears = "12/1/2021".toDogYears(),
                        humanYears = "12/1/2021".toHumanYears(),
                        weightFormat = WeightFormat.POUNDS,
                        dateFormat = DateFormat.AMERICAN,
                        shouldAnimate = true
                    ), Dog(
                        id = 2,
                        name = "Dog_2",
                        weight = 68.0,
                        profilePic = Uri.EMPTY,
                        birthDate = "12/2/2021",
                        dogYears = "12/2/2021".toDogYears(),
                        humanYears = "12/2/2021".toHumanYears(),
                        weightFormat = WeightFormat.POUNDS,
                        dateFormat = DateFormat.AMERICAN,
                        shouldAnimate = true
                    ), Dog(
                        id = 3,
                        name = "Dog_3",
                        weight = 68.0,
                        profilePic = Uri.EMPTY,
                        birthDate = "12/3/2021",
                        dogYears = "12/3/2021".toDogYears(),
                        humanYears = "12/3/2021".toHumanYears(),
                        weightFormat = WeightFormat.POUNDS,
                        dateFormat = DateFormat.AMERICAN,
                        shouldAnimate = true
                    )
                )
            )
        }

        @Singleton
        @Provides
        fun providesDogRepo(
            @Named("memory") memoryDataSource: DogsDataSource,
            @Named("disk") diskDataSource: DogsDataSource,
            @Named("computation") computationDispatcher: CoroutineDispatcher
        ): DogsRepo {
            return DogsRepoImpl(memoryDataSource, diskDataSource, computationDispatcher)
        }
    }

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
