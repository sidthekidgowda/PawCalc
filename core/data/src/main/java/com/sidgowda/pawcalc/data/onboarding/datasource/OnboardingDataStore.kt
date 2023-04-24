package com.sidgowda.pawcalc.data.onboarding.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val PREFERENCES_KEY = "onboarding_preferences"

internal class OnboardingDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : OnboardingDataSource {

    override val onboardingState: Flow<OnboardingState> = dataStore.data.map { preferences ->
        val onboardingState = preferences[booleanPreferencesKey(PREFERENCES_KEY)] ?: false
        if (onboardingState) OnboardingState.Onboarded else OnboardingState.NotOnboarded
    }.catch { exception ->
        if (exception is IOException) {
            emit(OnboardingState.NotOnboarded)
        } else {
            throw exception
        }
    }

    override suspend fun setUserOnboarded() {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(PREFERENCES_KEY)] = true
        }
    }
}
