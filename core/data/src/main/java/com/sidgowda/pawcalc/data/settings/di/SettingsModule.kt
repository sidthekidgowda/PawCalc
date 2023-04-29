package com.sidgowda.pawcalc.data.settings.di

import com.sidgowda.pawcalc.data.settings.datasource.CachedSettingsDataSource
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepoImpl
import com.sidgowda.pawcalc.db.settings.SettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun providesSettingsRepo(
        settingsDataSource: SettingsDataSource,
        @Named("io") ioDispatcher: CoroutineDispatcher
    ): SettingsRepo {
        return SettingsRepoImpl(settingsDataSource, ioDispatcher)
    }

    @Provides
    @Singleton
    fun providesSettingsDataSource(
        settingsDao: SettingsDao,
        @Named("io") ioDispatcher: CoroutineDispatcher
    ): SettingsDataSource {
        return CachedSettingsDataSource(settingsDao, ioDispatcher)
    }
}
