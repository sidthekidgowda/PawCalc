package com.sidgowda.pawcalc.doginput.model

import android.net.Uri

data class DogInputState(
    val profilePic: Uri? = null,
    val name: String = "",
    val weight: String = "",
    val birthDate: String = "",
    val inputRequirements: List<DogInputRequirements> = emptyList(),
    val inputMode: DogInputMode = DogInputMode.NEW_DOG,
    val isError: Boolean = false
) {
    fun isInputValid(): Boolean {
        return name.isNotEmpty() &&
                weight.isNotEmpty() &&
                birthDate.isNotEmpty() &&
                profilePic != null &&
                inputRequirements.containsAll(DogInputRequirements.values().toList())
    }
}
