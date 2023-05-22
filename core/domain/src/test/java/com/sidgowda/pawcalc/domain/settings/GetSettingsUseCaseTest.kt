package com.sidgowda.pawcalc.domain.settings

import app.cash.turbine.test
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetSettingsUseCaseTest {

    private lateinit var getSettingsUseCase: GetSettingsUseCase
    private lateinit var settingsRepo: SettingsRepo

    @Before
    fun setup() {
        settingsRepo = mockk()
        getSettingsUseCase = GetSettingsUseCase(settingsRepo)
    }

    @Test
    fun `when settings is requested, then settings is returned from repo`() = runTest {
        every { settingsRepo.settings() } returns flowOf(
            Settings(
                weightFormat = WeightFormat.KILOGRAMS,
                dateFormat = DateFormat.INTERNATIONAL,
                themeFormat = ThemeFormat.SYSTEM
            )
        )
        getSettingsUseCase.invoke().test {
            assertEquals(
                Settings(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.SYSTEM
                ), awaitItem()
            )
            awaitComplete()
        }
    }

    @Test
    fun `when settings repo emits multiple times, then use case must collect all items`() = runTest {
        every { settingsRepo.settings() } answers {
            flow {
                emit(
                    Settings(
                        weightFormat = WeightFormat.KILOGRAMS,
                        dateFormat = DateFormat.INTERNATIONAL,
                        themeFormat = ThemeFormat.SYSTEM
                    )
                )
                emit(
                    Settings(
                        weightFormat = WeightFormat.KILOGRAMS,
                        dateFormat = DateFormat.INTERNATIONAL,
                        themeFormat = ThemeFormat.DARK
                    )
                )
                emit(
                    Settings(
                        weightFormat = WeightFormat.POUNDS,
                        dateFormat = DateFormat.INTERNATIONAL,
                        themeFormat = ThemeFormat.DARK
                    )
                )
            }
        }
        getSettingsUseCase.invoke().test {
            assertEquals(
                Settings(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.SYSTEM
                ),
                awaitItem()
            )
            assertEquals(
                Settings(
                    weightFormat = WeightFormat.KILOGRAMS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.DARK
                ),
                awaitItem()
            )
            assertEquals(
                Settings(
                    weightFormat = WeightFormat.POUNDS,
                    dateFormat = DateFormat.INTERNATIONAL,
                    themeFormat = ThemeFormat.DARK
                ),
                awaitItem()
            )
            awaitComplete()
        }
    }
}
