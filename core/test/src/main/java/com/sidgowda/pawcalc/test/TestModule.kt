package com.sidgowda.pawcalc.test

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.sidgowda.pawcalc.data.onboarding.di.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.junit.rules.TemporaryFolder
import javax.inject.Named
import javax.inject.Singleton

object TestModule {

    @Module
    @TestInstallIn(
        components = [SingletonComponent::class],
        replaces = [DataStoreModule::class],
    )
    object TestDataStoreModule {

        @Provides
        @Singleton
        fun providesDataStore(
            @ApplicationContext context: Context,
            @Named("io") ioDispatcher: CoroutineDispatcher,
            tmpFolder: TemporaryFolder,
        ): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                migrations = emptyList(),
                scope = CoroutineScope(ioDispatcher + SupervisorJob()),
                produceFile = { tmpFolder.newFile("test_onboarding_data_store") }
            )
        }
    }
}
