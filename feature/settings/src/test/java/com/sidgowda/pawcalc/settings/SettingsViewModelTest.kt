package com.sidgowda.pawcalc.settings

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import com.sidgowda.pawcalc.domain.settings.UpdateSettingsUseCase
import com.sidgowda.pawcalc.settings.model.SettingsEvent
import com.sidgowda.pawcalc.settings.ui.SettingsViewModel
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var getSettingsUseCase: GetSettingsUseCase
    private lateinit var updateSettingsUseCase: UpdateSettingsUseCase
    private lateinit var viewModel: SettingsViewModel
    private lateinit var fakeSettingsRepo: FakeSettingsRepo


    @Before
    fun setup() {
        scope = TestScope()
        fakeSettingsRepo = FakeSettingsRepo()
        getSettingsUseCase = GetSettingsUseCase(fakeSettingsRepo)
        updateSettingsUseCase = UpdateSettingsUseCase(fakeSettingsRepo)
    }

    @Test
    fun `first item emitted should be default settings until settings is updated`() = scope.runTest {
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS
        )
    }

    @Test
    fun `when settings is updated then the next time viewmodel is initialized should get updated settings`() = scope.runTest {
        fakeSettingsRepo.updateSettings(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS))
            .also { advanceUntilIdle() }

        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS)
        )
    }

    @Test
    fun `when weight is changed then update settings is called`() = scope.runTest {
        val spyUpdateUseCase = spyk(updateSettingsUseCase)
        viewModel = SettingsViewModel(getSettingsUseCase, spyUpdateUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.WeightFormatChange(weightFormat = WeightFormat.KILOGRAMS))
            .also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS,
            DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS)
        )

        coVerify(exactly = 1) { spyUpdateUseCase.invoke(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS)) }
    }

    @Test
    fun `when weight is in kg and weight change is in kg, no update should happen`() = scope.runTest {
        fakeSettingsRepo.updateSettings(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS))
            .also { advanceUntilIdle() }
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.WeightFormatChange(weightFormat = WeightFormat.KILOGRAMS))
            .also { advanceUntilIdle() }

        // history should only have 1 weight
        history shouldContainExactly listOf(
            DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS)
        )
    }

    @Test
    fun `when weight is in kg and weight change is in lb, update should happen`() = scope.runTest {
        fakeSettingsRepo.updateSettings(DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS))
            .also { advanceUntilIdle() }
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.WeightFormatChange(weightFormat = WeightFormat.POUNDS))
            .also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS.copy(weightFormat = WeightFormat.KILOGRAMS),
            DEFAULT_SETTINGS
        )
    }

    @Test
    fun `when date format is american and changed to american, no update should happen`() = scope.runTest {
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.DateFormatChange(dateFormat = DateFormat.AMERICAN)).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS
        )
    }

    @Test
    fun `when date format is american and changed to international, then update should happen`() = scope.runTest {
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.DateFormatChange(dateFormat = DateFormat.INTERNATIONAL)).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS,
            DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL)
        )
    }

    @Test
    fun `when date format is international and changed to american, then update should happen`() = scope.runTest {
        fakeSettingsRepo.updateSettings(DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL))
            .also { advanceUntilIdle() }
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.DateFormatChange(dateFormat = DateFormat.AMERICAN)).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL),
            DEFAULT_SETTINGS
        )
    }

    @Test
    fun `verify when date is changed, update settings is invoked`() = scope.runTest {
        val spyUpdateUseCase = spyk(updateSettingsUseCase)
        viewModel = SettingsViewModel(getSettingsUseCase, spyUpdateUseCase)

        viewModel.handleEvent(SettingsEvent.DateFormatChange(dateFormat = DateFormat.INTERNATIONAL)).also { advanceUntilIdle() }

        coVerify(exactly = 1) { spyUpdateUseCase.invoke(DEFAULT_SETTINGS.copy(dateFormat = DateFormat.INTERNATIONAL)) }
    }

    @Test
    fun `when theme format is system and theme is changed to system, no update should happen`() = scope.runTest {
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.ThemeChange(theme = ThemeFormat.SYSTEM)).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS
        )
    }

    @Test
    fun `when theme format is system and theme is changed to dark, then update should happen`() = scope.runTest {
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.ThemeChange(theme = ThemeFormat.DARK)).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS,
            DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK)
        )
    }

    @Test
    fun `when theme format is system and theme is changed to light, then update should happen`() = scope.runTest {
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.ThemeChange(theme = ThemeFormat.LIGHT)).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS,
            DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.LIGHT)
        )
    }

    @Test
    fun `when theme format is dark initially and theme is changed to system, then update should happen`() = scope.runTest {
        fakeSettingsRepo.updateSettings(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK))
            .also { advanceUntilIdle() }
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(SettingsEvent.ThemeChange(theme = ThemeFormat.SYSTEM)).also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.DARK),
            DEFAULT_SETTINGS
        )
    }

    @Test
    fun `verify when theme is changed, update settings is invoked`() = scope.runTest {
        val spyUpdateUseCase = spyk(updateSettingsUseCase)
        viewModel = SettingsViewModel(getSettingsUseCase, spyUpdateUseCase)

        viewModel.handleEvent(SettingsEvent.ThemeChange(theme = ThemeFormat.LIGHT)).also { advanceUntilIdle() }

        coVerify(exactly = 1) { spyUpdateUseCase.invoke(DEFAULT_SETTINGS.copy(themeFormat = ThemeFormat.LIGHT)) }
    }

    @Test
    fun `when settings has changed previously to viewmodel being created again and settings is collected, it should emit most recent settings`() = scope.runTest {
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        viewModel.handleEvent(SettingsEvent.DateFormatChange(DateFormat.INTERNATIONAL))
        viewModel.handleEvent(SettingsEvent.WeightFormatChange(WeightFormat.KILOGRAMS))
        viewModel.handleEvent(SettingsEvent.ThemeChange(ThemeFormat.LIGHT))

        // recreate viewmodel
        viewModel = SettingsViewModel(getSettingsUseCase, updateSettingsUseCase)
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            DEFAULT_SETTINGS.copy(
                weightFormat = WeightFormat.KILOGRAMS,
                dateFormat = DateFormat.INTERNATIONAL,
                themeFormat = ThemeFormat.LIGHT
            )
        )
    }

    private fun SettingsViewModel.createStateHistory(): List<Settings> {
        val history = mutableListOf<Settings>()
        scope.backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.settings.toCollection(history)
        }
        return history
    }

    private companion object {
        val DEFAULT_SETTINGS = Settings(
            themeFormat = ThemeFormat.SYSTEM,
            dateFormat = DateFormat.AMERICAN,
            weightFormat = WeightFormat.POUNDS
        )
    }


}
