package com.sidgowda.pawcalc

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.sidgowda.pawcalc.newdog.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.onboarding.navigation.ONBOARDING_ROUTE

sealed class Destination(
    @StringRes val title: Int,
    val navIcon: ImageVector? = null,
    val actionIcon: ImageVector? = null
) {
    companion object {
        fun fromString(route: String): Destination {
            return when(route) {
                ONBOARDING_ROUTE -> Onboarding
                SETTINGS_ROUTE -> Settings
                NEW_DOG_SCREEN_ROUTE -> NewDog
                else -> DogList
            }
        }
    }
    object Onboarding: Destination(
        title = R.string.title_home,
        actionIcon = Icons.Default.Settings
    )
    object Settings: Destination(title = R.string.title_settings)
    object DogList: Destination(
        title = R.string.title_home,
        actionIcon = Icons.Default.Settings
    )

    object NewDog: Destination(
        title = R.string.title_add_dog,
        navIcon = Icons.Default.Close,
        actionIcon = Icons.Default.Settings
    )
}
