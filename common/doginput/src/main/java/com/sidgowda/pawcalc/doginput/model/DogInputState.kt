package com.sidgowda.pawcalc.doginput.model

import android.net.Uri

data class DogInputState(
    val isLoading: Boolean = false,
    val profilePic: Uri? = null,
    val name: String = "",
    val isNameValid: Boolean = true,
    val weight: String = "",
    val isWeightValid: Boolean = true,
    val birthDate: String = "",
    val isBirthDateValid: Boolean = true,
    val inputRequirements: Set<DogInputRequirements> = emptySet(),
    val inputMode: DogInputMode = DogInputMode.NEW_DOG
) {
    fun isInputValid(): Boolean = inputRequirements.containsAll(
        DogInputRequirements.values().toList()
    )
}
