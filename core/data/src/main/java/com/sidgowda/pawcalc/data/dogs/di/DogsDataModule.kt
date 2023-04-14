package com.sidgowda.pawcalc.data.dogs.di

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDiskDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsMemoryDataSource
import com.sidgowda.pawcalc.db.dog.DogsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DogsDataModule {

    @Named("memory")
    @Singleton
    @Provides
    fun providesMemoryDogsDataSource(): DogsDataSource {
        return DogsMemoryDataSource()
    }

    @Named("disk")
    @Singleton
    @Provides
    fun providesDiskDogsDataSource(
        dogsDao: DogsDao
    ): DogsDataSource {
        return DogsDiskDataSource(dogsDao)
    }
}
