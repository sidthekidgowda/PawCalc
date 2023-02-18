package com.sidgowda.pawcalc

sealed class Screens(val route: String) {
    object Welcome: Screens("welcome_screen")
    object Settings: Screens("settings_screen")
    object DogList: Screens("dog_list_screen")
}
