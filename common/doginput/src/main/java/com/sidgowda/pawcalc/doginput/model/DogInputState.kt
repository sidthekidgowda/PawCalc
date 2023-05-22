package com.sidgowda.pawcalc.doginput.model

import android.net.Uri
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat

data class DogInputState(
    val profilePic: Uri? = null,
    val name: String = "",
    val isNameValid: Boolean = true,
    val weight: String = "",
    val weightFormat: WeightFormat = WeightFormat.POUNDS,
    val isWeightValid: Boolean = true,
    val birthDate: String = "",
    val dateFormat: DateFormat = DateFormat.AMERICAN,
    val isBirthDateValid: Boolean = true,
    val birthDateDialogShown: Boolean = false,
    val inputRequirements: Set<DogInputRequirements> = emptySet()
) {
    fun isInputValid(): Boolean = inputRequirements.containsAll(
        DogInputRequirements.values().toList()
    )
}
