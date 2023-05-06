package com.sidgowda.pawcalc

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.sidgowda.pawcalc.data.modules.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TemporaryFolder
import javax.inject.Named
import javax.inject.Singleton

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
        @Named("ioScope") coroutineScope: CoroutineScope
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = emptyList(),
            scope = coroutineScope,
            produceFile = { tmpFolder.newFile("test_onboarding_data_store.preferences_pb") }
        )
    }
}
