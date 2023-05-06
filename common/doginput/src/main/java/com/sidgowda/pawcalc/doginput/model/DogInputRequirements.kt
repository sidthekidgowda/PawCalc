package com.sidgowda.pawcalc.doginput.model

import android.net.Uri
import com.sidgowda.pawcalc.common.settings.WeightFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

enum class DogInputRequirements {
    OnePicture,
    NameBetweenZeroAndFifty,
    WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg,
    BirthDate
}

fun MutableStateFlow<DogInputState>.updateName(name: String) {
    val inputRequirements = listOf(DogInputRequirements.NameBetweenZeroAndFifty)
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
    val inputRequirements = listOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
    val weightAsDouble: Double? = weight.toDoubleOrNull()
    val weightUnderLimit =
        weightAsDouble != null && weightAsDouble > 0.0 && if (value.weightFormat == WeightFormat.POUNDS) weightAsDouble <= 500 else weightAsDouble <= 225
    val isWeightValid = weight.isNotEmpty() && weightUnderLimit
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
            inputRequirements = it.inputRequirements.plus(DogInputRequirements.OnePicture)
        )
    }
}

fun MutableStateFlow<DogInputState>.updateBirthDate(birthDate: String) {
    update {
        it.copy(
            birthDate = birthDate,
            isBirthDateValid = birthDate.isNotEmpty() || (birthDate.isEmpty() && !it.birthDateDialogShown),
            inputRequirements = if (birthDate.isNotEmpty()) {
                it.inputRequirements.plus(DogInputRequirements.BirthDate)
            } else {
                it.inputRequirements.minus(DogInputRequirements.BirthDate)
            }
        )
    }
}

fun MutableStateFlow<DogInputState>.updateBirthDateDialogShown() {
    update {
        it.copy(
            birthDateDialogShown = true,
            isBirthDateValid = it.birthDate.isNotEmpty()
        )
    }
}
