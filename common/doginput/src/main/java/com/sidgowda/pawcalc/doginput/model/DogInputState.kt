package com.sidgowda.pawcalc.doginput.model

import android.net.Uri

data class DogInputState(
    val profilePic: Uri? = null,
    val name: String = "",
    val nameValid: Boolean = true,
    val weight: String = "",
    val weightValid: Boolean = true,
    val hasUserClickedOnBirthDate: Boolean = false,
    val birthDate: String = "",
    val birthDateValid: Boolean = true,
    val inputRequirements: Set<DogInputRequirements> = emptySet(),
    val inputMode: DogInputMode = DogInputMode.NEW_DOG
) {
    fun isInputValid(): Boolean = inputRequirements.containsAll(
        DogInputRequirements.values().toList()
    )
}
