package com.sidgowda.pawcalc

import androidx.annotation.StringRes
import com.sidgowda.pawcalc.welcome.navigation.WELCOME_SCREEN_ROUTE

sealed class Destination(
    @StringRes val title: Int
) {
    companion object {
        fun fromString(route: String): Destination {
            return when(route) {
                WELCOME_SCREEN_ROUTE -> Welcome
                SETTINGS_ROUTE -> Settings
                else -> DogList
            }
        }
    }
    object Welcome: Destination(title = R.string.title_home)
    object Settings: Destination(title = R.string.title_settings)
    object DogList: Destination(title = R.string.title_home)
}
