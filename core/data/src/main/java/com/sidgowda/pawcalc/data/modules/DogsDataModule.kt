package com.sidgowda.pawcalc.data.modules

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDiskDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepoImpl
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.db.dog.DogsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DogsDataModule {

    @Named("memory")
    @Singleton
    @Provides
    fun providesMemoryDogsDataSource(settingsDataSource: SettingsDataSource): DogsDataSource {
        return DogsMemoryDataSource(settingsDataSource)
    }

    @Named("disk")
    @Singleton
    @Provides
    fun providesDiskDogsDataSource(
        dogsDao: DogsDao
    ): DogsDataSource {
        return DogsDiskDataSource(dogsDao)
    }

    @Singleton
    @Provides
    fun providesDogRepo(
        @Named ("memory") memoryDataSource: DogsDataSource,
        @Named ("disk") diskDataSource: DogsDataSource,
        @Named ("computation") computationDispatcher: CoroutineDispatcher
    ): DogsRepo {
        return DogsRepoImpl(memoryDataSource, diskDataSource, computationDispatcher)
    }
}
