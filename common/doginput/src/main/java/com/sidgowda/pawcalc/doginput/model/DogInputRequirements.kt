package com.sidgowda.pawcalc.doginput.model

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

enum class DogInputRequirements {
    ONE_PICTURE,
    NAME_BETWEEN_ONE_AND_FIFTY,
    WEIGHT_MORE_THAN_ZERO_AND_VALID_NUMBER,
    BIRTH_DATE
}

fun MutableStateFlow<DogInputState>.updateName(name: String) {
    val inputRequirements = listOf(DogInputRequirements.NAME_BETWEEN_ONE_AND_FIFTY)
    // we don't want show error until we have one character
    val isNameValidForTextInput = name.length <= 50
    val isNameValid = name.isNotEmpty() && isNameValidForTextInput
    update {
        it.copy(
            name = name,
            isNameValid = isNameValidForTextInput,
            inputRequirements = if (isNameValid) {
                it.inputRequirements.plus(inputRequirements)
            } else {
                it.inputRequirements.minus(inputRequirements)
            }
        )
    }
}

fun MutableStateFlow<DogInputState>.updateWeight(weight: String) {
    val inputRequirements = listOf(DogInputRequirements.WEIGHT_MORE_THAN_ZERO_AND_VALID_NUMBER)
    val weightAsDouble: Double? = weight.toDoubleOrNull()
    val isWeightValid = weight.isNotEmpty() && weightAsDouble != null && weightAsDouble > 0.0
    // we don't show error until we have one character
    val isWeightValidForTextInput = weight.isEmpty() || isWeightValid
    update {
        it.copy(
            weight = weight,
            isWeightValid = isWeightValidForTextInput,
            inputRequirements = if (isWeightValid) {
                it.inputRequirements.plus(inputRequirements)
            } else {
                it.inputRequirements.minus(inputRequirements)
            }
        )
    }
}

fun MutableStateFlow<DogInputState>.updateProfilePic(pictureUrl: Uri) {
    update {
        it.copy(
            profilePic = pictureUrl,
            inputRequirements = it.inputRequirements.plus(DogInputRequirements.ONE_PICTURE)
        )
    }
}

fun MutableStateFlow<DogInputState>.updateBirthDate(birthDate: String) {
    update {
        it.copy(
            birthDate = birthDate,
            isBirthDateValid = true,
            inputRequirements = it.inputRequirements.plus(DogInputRequirements.BIRTH_DATE)
        )
    }
}

fun MutableStateFlow<DogInputState>.updateBirthDateDialogShown() {
    update {
        it.copy(
            isBirthDateValid = it.birthDate.isNotEmpty()
        )
    }
}
