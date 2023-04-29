package com.sidgowda.pawcalc.data.settings

import dagger.Provides
import javax.inject.Singleton

object SettingsModule {

    @Provides
    @Singleton
    fun providesSettingsRepo(): SettingsRepo {
        return SettingsRepoImpl()
    }
}
