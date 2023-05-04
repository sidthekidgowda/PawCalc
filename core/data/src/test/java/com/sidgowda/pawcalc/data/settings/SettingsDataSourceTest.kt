package com.sidgowda.pawcalc.data.settings

import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.datasource.CachedSettingsDataSource
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.model.toSettings
import com.sidgowda.pawcalc.db.settings.SettingsDao
import com.sidgowda.pawcalc.db.settings.SettingsEntity
import io.kotest.matchers.shouldBe
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsDataSourceTest {

    private lateinit var settingsDataSource: SettingsDataSource
    private lateinit var scope: TestScope
    private lateinit var settingsDao: SettingsDao
    private lateinit var capturedSettings: CapturingSlot<SettingsEntity>

    @Before
    fun setup() {
        settingsDao = mockk()
        capturedSettings = slot()
        scope = TestScope()
    }

    @Test
    fun `when no settings exist then default settings is added to database`() = scope.runTest {
        every { settingsDao.settings() } returns flowOf(emptyList())
        coEvery { settingsDao.insert(capture(capturedSettings)) } just runs
        settingsDataSource = CachedSettingsDataSource(
            settingsDao = settingsDao,
            scope = scope
        )

        advanceUntilIdle()

        coVerify(exactly = 1) { settingsDao.insert(DEFAULT_SETTINGS_ENTITY) }
        capturedSettings.captured shouldBe DEFAULT_SETTINGS_ENTITY
    }

    @Test
    fun `when no settings exist then default settings is added and emitted`() = scope.runTest {
        every { settingsDao.settings() } returns flowOf(emptyList())
        coEvery { settingsDao.insert(any()) } just runs
        settingsDataSource = CachedSettingsDataSource(
            settingsDao = settingsDao,
            scope = scope
        )

        settingsDataSource.settings().test {
            assertEquals(
                DEFAULT_SETTINGS_ENTITY.toSettings(),
                awaitItem()
            )
        }
    }

    @Test
    fun `when database emits error then default settings is added and emitted`() = scope.runTest {
       every { settingsDao.settings() } returns flow {
            throw IOException()
        }
        coEvery { settingsDao.insert(capture(capturedSettings)) } just runs
        settingsDataSource = CachedSettingsDataSource(
            settingsDao = settingsDao,
            scope = scope
        )

        advanceUntilIdle()

        coVerify(exactly = 1) { settingsDao.insert(DEFAULT_SETTINGS_ENTITY) }
        capturedSettings.captured shouldBe DEFAULT_SETTINGS_ENTITY
    }

    @Test
    fun `when settings exist in database then settings is returned but not added to database`() = scope.runTest {
        every { settingsDao.settings() } returns flowOf(
            listOf(
                DEFAULT_SETTINGS_ENTITY.copy(
                    weightFormat = WeightFormat.KILOGRAMS
                )
            )
        )
        settingsDataSource = CachedSettingsDataSource(
            settingsDao = settingsDao,
            scope = scope
        )

        settingsDataSource.settings().test {
            assertEquals(
                DEFAULT_SETTINGS_ENTITY.toSettings().copy(
                    weightFormat = WeightFormat.KILOGRAMS
                ), awaitItem()
            )
        }
        coVerify(exactly = 0) { settingsDao.insert(any()) }
    }

    @Test
    fun `when update settings is called, then it should overwrite current settings and be returned`() = scope.runTest {
        every { settingsDao.settings() } returns flowOf(
            listOf(
                DEFAULT_SETTINGS_ENTITY.copy(
                    themeFormat = ThemeFormat.LIGHT
                )
            )
        )
        coEvery { settingsDao.insert(any()) } just runs

        settingsDataSource = CachedSettingsDataSource(
            settingsDao = settingsDao,
            scope = scope
        )

        settingsDataSource.settings().test {
            assertEquals(
                DEFAULT_SETTINGS_ENTITY.copy(themeFormat = ThemeFormat.LIGHT).toSettings(), awaitItem()
            )
            settingsDataSource.updateSettings(
                DEFAULT_SETTINGS_ENTITY.toSettings().copy(
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.LIGHT
                )
            )
            assertEquals(
                DEFAULT_SETTINGS_ENTITY.toSettings().copy(
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.LIGHT
                ), awaitItem()
            )
        }
    }

    @Test
    fun `new collectors should get last settings emitted`() = scope.runTest {
        every { settingsDao.settings() } returns flowOf(
            listOf(
                DEFAULT_SETTINGS_ENTITY.copy(
                    weightFormat = WeightFormat.KILOGRAMS
                )
            )
        )
        coEvery { settingsDao.insert(any()) } just runs

        settingsDataSource = CachedSettingsDataSource(
            settingsDao = settingsDao,
            scope = scope
        )
        settingsDataSource.settings().test {
            assertEquals(
                DEFAULT_SETTINGS_ENTITY.copy(weightFormat = WeightFormat.KILOGRAMS).toSettings(), awaitItem()
            )
            settingsDataSource.updateSettings(
                DEFAULT_SETTINGS_ENTITY.toSettings().copy(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.DARK
                )
            )
            assertEquals(
                DEFAULT_SETTINGS_ENTITY.toSettings().copy(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.DARK
                ), awaitItem()
            )
        }

        //new collector
        settingsDataSource.settings().test {
            assertEquals(
                DEFAULT_SETTINGS_ENTITY.toSettings().copy(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.DARK
                ), awaitItem()
            )
        }
    }

    @Test
    fun `updated settings should have same primary key as previous settings`() = scope.runTest {
        every { settingsDao.settings() } returns flowOf(
            listOf(
                DEFAULT_SETTINGS_ENTITY
            )
        )
        coEvery { settingsDao.insert(capture(capturedSettings)) } just runs
        settingsDataSource = CachedSettingsDataSource(
            settingsDao = settingsDao,
            scope = scope
        )

        settingsDataSource.updateSettings(
            Settings(
                weightFormat = WeightFormat.KILOGRAMS,
                themeFormat = ThemeFormat.SYSTEM,
                dateFormat = DateFormat.INTERNATIONAL
            )
        )

        capturedSettings.captured shouldBe SettingsEntity(
            id = 1,
            weightFormat = WeightFormat.KILOGRAMS,
            themeFormat = ThemeFormat.SYSTEM,
            dateFormat = DateFormat.INTERNATIONAL
        )
    }


    private companion object {
        private val DEFAULT_SETTINGS_ENTITY = SettingsEntity(
            id = 1,
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    }
}
