package com.sidgowda.pawcalc.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.navigation.SETTINGS_SCREEN_ROUTE
import com.sidgowda.pawcalc.settings.R
import com.sidgowda.pawcalc.settings.model.SettingsEvent
import com.sidgowda.pawcalc.ui.theme.Grey200
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
internal fun Settings() {
    val viewModel = hiltViewModel<SettingsViewModel>()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    
    SettingsScreen(
        settings = settings,
        onSettingsEvent = viewModel::handleEvent
    )
}

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    settings: Settings,
    onSettingsEvent: (SettingsEvent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(PawCalcTheme.colors.surface)
            .testTag(SETTINGS_SCREEN_ROUTE),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChooseDate(
            dateFormat = settings.dateFormat,
            onSettingsEvent = onSettingsEvent
        )
        ChooseWeight(
            weightFormat = settings.weightFormat,
            onSettingsEvent = onSettingsEvent
        )
        ChooseTheme(
            themeFormat = settings.themeFormat,
            onSettingsEvent = onSettingsEvent
        )
    }
}

@Composable
internal fun ChooseDate(
    modifier: Modifier = Modifier,
    dateFormat: DateFormat,
    onSettingsEvent: (SettingsEvent) -> Unit
) {
    val dateOptions = stringArrayResource(id = R.array.settings_options_date)
    val header = stringResource(id = R.string.settings_date_header)
    SettingsGroup(
        modifier = modifier,
        text = header,
        radioGroupOptions = dateOptions,
        accessibilityOptions = stringArrayResource(id = R.array.cd_settings_options_date),
        selectedOptionIndex = dateFormat.index,
        onOptionSelected = { index ->
            onSettingsEvent(SettingsEvent.DateFormatChange(DateFormat.from(index)))

        }
    )
}

@Composable
internal fun ChooseWeight(
    modifier: Modifier = Modifier,
    weightFormat: WeightFormat,
    onSettingsEvent: (SettingsEvent) -> Unit
) {
    val weightOptions = stringArrayResource(id = R.array.settings_options_weight)
    val header = stringResource(id = R.string.settings_weight_header)
    SettingsGroup(
        modifier = modifier,
        text = header,
        radioGroupOptions = weightOptions,
        accessibilityOptions = stringArrayResource(id = R.array.cd_settings_options_weight),
        selectedOptionIndex = weightFormat.index,
        onOptionSelected = { index ->
            onSettingsEvent(SettingsEvent.WeightFormatChange(WeightFormat.from(index)))
        }
    )
}

@Composable
internal fun ChooseTheme(
    modifier: Modifier = Modifier,
    themeFormat: ThemeFormat,
    onSettingsEvent: (SettingsEvent) -> Unit
) {
    val themeOptions = stringArrayResource(id = R.array.settings_options_theme)
    val header = stringResource(id = R.string.settings_theme_header)
    SettingsGroup(
        modifier = modifier,
        text = header,
        radioGroupOptions = themeOptions,
        accessibilityOptions = stringArrayResource(id = R.array.cd_settings_options_theme),
        selectedOptionIndex = themeFormat.index,
        onOptionSelected = { index ->
            onSettingsEvent(SettingsEvent.ThemeChange(ThemeFormat.from(index)))
        }
    )
}

@Composable
internal fun SettingsGroup(
    modifier: Modifier = Modifier,
    text: String,
    radioGroupOptions: Array<String>,
    accessibilityOptions: Array<String>,
    selectedOptionIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Surface {
        Column(modifier = modifier.fillMaxWidth()) {
            Header(text = text)
            Spacer(modifier = Modifier.height(10.dp))
            RadioGroup(
                radioGroupOptions = radioGroupOptions,
                accessibilityOptions = accessibilityOptions,
                selectedOptionIndex = selectedOptionIndex,
                onOptionSelected = { optionIndex ->
                    onOptionSelected(optionIndex)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
internal fun Header(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        style = PawCalcTheme.typography.h5,
        color = Color.Black,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .background(Grey200)
            .padding(18.dp)
    )
}


@Composable
internal fun RadioGroup(
    modifier: Modifier = Modifier,
    selectedOptionIndex: Int,
    radioGroupOptions: Array<String>,
    accessibilityOptions: Array<String>,
    onOptionSelected: (option: Int) -> Unit
) {
    radioGroupOptions.forEachIndexed { index, option ->
        val selected = selectedOptionIndex == index
        val hintsEnabledState = if (selected) {
            stringResource(id = R.string.cd_settings_selected_option, accessibilityOptions[index])
        } else {
            stringResource(id = R.string.cd_settings_not_selected_option, accessibilityOptions[index])
        }
        Row(
            modifier = modifier
                .selectable(
                    selected = selected,
                    onClick = {
                        onOptionSelected(index)
                    },
                    role = Role.RadioButton
                )
                .semantics {
                    stateDescription = hintsEnabledState
                }
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            RadioButton(
                selected = selected,
                onClick = null
            )
            Text(
                modifier = Modifier.padding(
                    start = 12.dp
                ),
                text = option,
                style = PawCalcTheme.typography.body1,
                color = PawCalcTheme.colors.onBackground
            )
        }
    }
}

//------Preview-------------------------------------------------------------------------------------
@LightDarkPreview
@Composable
fun PreviewSettingsScreen() {
    PawCalcTheme {
        SettingsScreen(
            settings = Settings(
                weightFormat = com.sidgowda.pawcalc.common.settings.WeightFormat.POUNDS,
                dateFormat = DateFormat.AMERICAN,
                themeFormat = ThemeFormat.SYSTEM
            ),
            onSettingsEvent = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewHeader() {
    PawCalcTheme {
        Header(text = "Weight Format")
    }
}
