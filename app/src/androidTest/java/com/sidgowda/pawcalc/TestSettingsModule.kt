package com.sidgowda.pawcalc

import com.sidgowda.pawcalc.data.modules.SettingsModule
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

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
