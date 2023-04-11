package com.sidgowda.pawcalc.data.onboarding.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val DATA_STORE_KEY = "onboarding_data_store"
private const val PREFERENCES_KEY = "onboarding_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_KEY)

class OnboardingDataSourceImpl @Inject constructor(
    private val context: Context
) : OnboardingDataSource {

    override val onboardingState: Flow<OnboardingState> = context.dataStore.data.map { preferences ->
        val onboardingState = preferences[booleanPreferencesKey(PREFERENCES_KEY)] ?: false
        if (onboardingState) OnboardingState.Onboarded else OnboardingState.NotOnboarded
    }

    override suspend fun setUserOnboarded() {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(PREFERENCES_KEY)] = true
        }
    }
}
