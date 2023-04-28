package com.sidgowda.pawcalc.data.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataStore
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

private const val TEST_DATASTORE_NAME: String = "test_datastore"

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class OnboardingDataStoreTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var onboardingDataStore: OnboardingDataStore
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var scope: TestScope
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var context: Context

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        scope = TestScope(testDispatcher + Job())
        context = ApplicationProvider.getApplicationContext()
        dataStore = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { context.preferencesDataStoreFile(TEST_DATASTORE_NAME) }
        )
        onboardingDataStore = OnboardingDataStore(dataStore)
    }

    @Test
    fun `onboarding data store should return NotOnboarded`() = runTest {
        onboardingDataStore.onboardingState.test {
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
        }
    }

    @Test
    fun `when onboarding data store has key, it should return Onboarded`() = runTest {
        dataStore.edit {
            it[booleanPreferencesKey("onboarding_preferences")] = true
        }

        onboardingDataStore.onboardingState.test {
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
    }

    @Test
    fun `when onboarding data store throws IOException, it should return NotOnboarded`() = runTest {
        dataStore = mockk()
        every { dataStore.data } returns flow {
            throw IOException()
        }
        onboardingDataStore = OnboardingDataStore(dataStore)

        onboardingDataStore.onboardingState.test {
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when onboarding data store throws non IOException, it should be thrown`() = runTest {
        dataStore = mockk()
        every { dataStore.data } returns flow {
            throw IllegalStateException("Error in Data store")
        }
        onboardingDataStore = OnboardingDataStore(dataStore)

        onboardingDataStore.onboardingState.test {
            assertEquals("Error in Data store", awaitError().message)
        }
    }

    @Test
    fun `when user has onboarded then onboarded state should be emitted`() = runTest {
        onboardingDataStore.onboardingState.test {
            assertEquals(OnboardingState.NotOnboarded, awaitItem())
            onboardingDataStore.setUserOnboarded()
            assertEquals(OnboardingState.Onboarded, awaitItem())
        }
    }
}
