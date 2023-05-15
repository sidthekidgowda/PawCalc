package com.sidgowda.pawcalc.db.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.db.PawCalcDatabase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SettingsDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PawCalcDatabase
    private lateinit var settingsDao: SettingsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PawCalcDatabase::class.java
        ).build()
        settingsDao = database.settingsDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun assertDatabaseIsEmpty() = runTest {
        settingsDao.settings().test {
            assertEquals(emptyList<SettingsEntity>(), awaitItem())
        }
    }

    @Test
    fun givenNoSettingsInDatabase_whenSettingsIsAdded_thenDatabaseShouldHaveSettings() = runTest {
        settingsDao.settings().test {
            assertEquals(emptyList<SettingsEntity>(), awaitItem())
            settingsDao.insert(
                SettingsEntity(
                    id = 1,
                    weightFormat = WeightFormat.POUNDS,
                    dateFormat = DateFormat.AMERICAN,
                    themeFormat = ThemeFormat.SYSTEM
                )
            )
            assertEquals(
                listOf(
                    SettingsEntity(
                        id = 1,
                        weightFormat = WeightFormat.POUNDS,
                        dateFormat = DateFormat.AMERICAN,
                        themeFormat = ThemeFormat.SYSTEM
                    )
                ), awaitItem()
            )
        }
    }

    @Test
    fun whenSettingsExistAndNewSettingsIsAddedWithSamePrimaryKey_thenNewSettingsWillOverwriteCurrentSettings() = runTest {
        settingsDao.insert(
            SettingsEntity(
                id = 1,
                weightFormat = WeightFormat.POUNDS,
                dateFormat = DateFormat.AMERICAN,
                themeFormat = ThemeFormat.SYSTEM
            )
        )
        settingsDao.settings().test {
            assertEquals(
                listOf(
                    SettingsEntity(
                        id = 1,
                        weightFormat = WeightFormat.POUNDS,
                        dateFormat = DateFormat.AMERICAN,
                        themeFormat = ThemeFormat.SYSTEM
                    )
                ), awaitItem()
            )
            settingsDao.insert(
                SettingsEntity(
                    id = 1,
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.SYSTEM
                )
            )
            assertEquals(
                listOf(
                    SettingsEntity(
                        id = 1,
                        weightFormat = WeightFormat.KILOGRAMS,
                        dateFormat = DateFormat.INTERNATIONAL,
                        themeFormat = ThemeFormat.SYSTEM
                    )
                ), awaitItem()
            )
        }
    }
}
