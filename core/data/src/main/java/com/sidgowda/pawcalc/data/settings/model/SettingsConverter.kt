package com.sidgowda.pawcalc.data.settings.model

import com.sidgowda.pawcalc.db.settings.SettingsEntity

fun SettingsEntity.toSettings(): Settings {
    return Settings(
        weightFormat = weightFormat,
        dateFormat = dateFormat,
        themeFormat = themeFormat
    )
}

fun Settings.toSettingsEntity(): SettingsEntity {
    // id is 1 as we will have only 1 settings entity instance per app
    // any updates will replace the existing entity
    return SettingsEntity(
        id = 1,
        weightFormat = weightFormat,
        dateFormat = dateFormat,
        themeFormat = themeFormat
    )
}
