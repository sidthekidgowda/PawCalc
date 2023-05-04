package com.sidgowda.pawcalc.domain.settings

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateSettingsUseCaseTest {

    private lateinit var updateSettingsUseCase: UpdateSettingsUseCase
    private lateinit var settingsRepo: SettingsRepo
    private lateinit var capturedSettings: CapturingSlot<Settings>

    @Before
    fun setup() {
        settingsRepo = mockk()
        capturedSettings = slot()
        coEvery { settingsRepo.updateSettings(capture(capturedSettings)) } just runs
        updateSettingsUseCase = UpdateSettingsUseCase(settingsRepo)
    }

    @Test
    fun `verify updateSettings updated correct settings`() = runTest {
        updateSettingsUseCase.invoke(DEFAULT_SETTINGS)
        coVerify(exactly = 1) { settingsRepo.updateSettings(DEFAULT_SETTINGS) }

        capturedSettings.captured shouldBe DEFAULT_SETTINGS
    }

    @Test
    fun `verify updateSettings updated with Weight Format Kilograms`() = runTest {
        updateSettingsUseCase.invoke(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS))
        coVerify(exactly = 1) { settingsRepo.updateSettings(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS)) }

        capturedSettings.captured shouldBe DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS)
    }

    @Test
    fun `verify updateSettings updated with Date Format International`() = runTest {
        updateSettingsUseCase.invoke(DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL))
        coVerify(exactly = 1) { settingsRepo.updateSettings(DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL)) }

        capturedSettings.captured shouldBe DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL)
    }

    @Test
    fun `verify updateSettings updated with Theme Dark`() = runTest {
        updateSettingsUseCase.invoke(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK))
        coVerify(exactly = 1) { settingsRepo.updateSettings(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK)) }

        capturedSettings.captured shouldBe DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK)
    }

    private companion object {

        private val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.INTERNATIONAL,
            themeFormat = ThemeFormat.SYSTEM
        )
    }
}
