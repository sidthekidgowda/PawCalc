package com.sidgowda.pawcalc.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.sidgowda.pawcalc.R
import com.sidgowda.pawcalc.SETTINGS_ROUTE
import com.sidgowda.pawcalc.editdog.EDIT_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.newdog.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.onboarding.navigation.ONBOARDING_ROUTE

sealed class Destination(
    @StringRes val title: Int,
    val navIcon: ImageVector? = null,
    @StringRes val navIconContentDescription: Int = 0,
    val actionIcon: ImageVector? = null,
    @StringRes val actionIconContentDescription: Int = 0
) {
    companion object {
        fun fromString(route: String): Destination {
            return when(route) {
                ONBOARDING_ROUTE -> Onboarding
                SETTINGS_ROUTE -> Settings
                NEW_DOG_SCREEN_ROUTE -> NewDog
                EDIT_DOG_SCREEN_ROUTE -> EditDog
                else -> DogList
            }
        }
    }
    object Onboarding : Destination(
        title = R.string.title_home,
        actionIcon = Icons.Default.Settings,
        actionIconContentDescription = R.string.cd_settings_action_icon
    )

    object Settings : Destination(
        title = R.string.title_settings,
        navIcon = Icons.Default.ArrowBack,
        navIconContentDescription = R.string.cd_press_back
    )
    object DogList : Destination(
        title = R.string.title_home,
        actionIcon = Icons.Default.Settings,
        actionIconContentDescription = R.string.cd_settings_action_icon
    )

    object NewDog : Destination(
        title = R.string.title_add_dog,
        navIcon = Icons.Default.Close,
        navIconContentDescription = R.string.cd_close_nav_icon,
        actionIcon = Icons.Default.Settings,
        actionIconContentDescription = R.string.cd_settings_action_icon
    )

    object EditDog: Destination(
        title = R.string.title_edit_dog,
        navIcon = Icons.Default.Close,
        navIconContentDescription = R.string.cd_close_nav_icon,
        actionIcon = Icons.Default.Settings,
        actionIconContentDescription = R.string.cd_settings_action_icon
    )
}
