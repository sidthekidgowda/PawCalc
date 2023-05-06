package com.sidgowda.pawcalc.data.settings

import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.fakes.FakeSettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepoImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepoTest {

    private lateinit var settingsRepo: SettingsRepo
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeSettingsDataSource: FakeSettingsDataSource
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        fakeSettingsDataSource = FakeSettingsDataSource()
        settingsRepo = SettingsRepoImpl(
            fakeSettingsDataSource,
            testDispatcher
        )
    }

    @Test
    fun `when settings is requested for first time then default settings is returned`() = testScope.runTest {
        settingsRepo.settings().test {
            assertEquals(DEFAULT_SETTINGS ,awaitItem())
        }
    }

    @Test
    fun `when update is called, then settings must be updated`() = testScope.runTest {
        settingsRepo.settings().test {
            assertEquals(DEFAULT_SETTINGS ,awaitItem())
            settingsRepo.updateSettings(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK))
            assertEquals(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK), awaitItem())
        }
    }

    @Test
    fun `new collectors should be most recent updated settings`() = testScope.runTest {
        settingsRepo.settings().test {
            assertEquals(DEFAULT_SETTINGS ,awaitItem())
            settingsRepo.updateSettings(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK))
            assertEquals(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK), awaitItem())
        }
        val newCollector: Flow<Settings> = settingsRepo.settings()
         newCollector.test {
             assertEquals(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK), awaitItem())
         }
    }

    private companion object {
        private val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    }
}
