package com.sidgowda.pawcalc

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepoImpl
import com.sidgowda.pawcalc.data.modules.DogsDataModule
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

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
        @Named("computation") computationDispatcher: CoroutineDispatcher
    ): DogsDataSource {
        return DogsMemoryDataSource(settingsDataSource, computationDispatcher)
    }

    @Named("disk")
    @Singleton
    @Provides
    fun providesDiskDogsDataSource(): DogsDataSource {
        return FakeDogsDiskDataSource()
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
