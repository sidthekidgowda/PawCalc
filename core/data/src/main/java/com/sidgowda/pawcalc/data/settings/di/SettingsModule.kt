package com.sidgowda.pawcalc.data.settings.di

import com.sidgowda.pawcalc.data.settings.CachedSettingsDataSource
import com.sidgowda.pawcalc.data.settings.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepoImpl
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
    fun providesSettingsDataSource(): SettingsDataSource {
        return CachedSettingsDataSource()
    }
}
