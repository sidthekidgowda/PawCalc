package com.sidgowda.pawcalc.newdog.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.domain.AddDogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewDogViewModel @Inject constructor(
    private val addDogUseCase: AddDogUseCase
) : ViewModel() {

    private val _inputState = MutableStateFlow(DogInputState())
    val inputState = _inputState.asStateFlow()

    fun handleEvent(dogInputEvent: DogInputEvent) {
        when (dogInputEvent) {
            is DogInputEvent.PicChanged -> updatePicture(dogInputEvent.pictureUrl)
            is DogInputEvent.NameChanged -> updateName(dogInputEvent.name)
            is DogInputEvent.WeightChanged -> updateWeight(dogInputEvent.weight)
            is DogInputEvent.BirthDateChanged -> updateBirthDate(dogInputEvent.birthDate)
            is DogInputEvent.BirthDateDialogShown -> updateBirthDateDialogShown()
            is DogInputEvent.SavingInfo -> saveDogInfo()
        }
    }

    private fun updatePicture(picture: Uri) {
        _inputState.update {
            it.copy(
                profilePic = picture,
                inputRequirements = it.inputRequirements.plus(DogInputRequirements.ONE_PICTURE)
            )
        }
    }

    private fun updateName(name: String) {
        val inputRequirements = listOf(DogInputRequirements.NAME_BETWEEN_ONE_AND_FIFTY)
        val isNameValidForTextInput = name.length <= 50 // we don't want show error until we have one character
        val isNameValid = name.isNotEmpty() && isNameValidForTextInput
        _inputState.update {
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

    private fun updateWeight(weight: String) {
        val inputRequirements = listOf(DogInputRequirements.WEIGHT_MORE_THAN_ZERO_AND_VALID_NUMBER)
        val weightAsDouble: Double? = weight.toDoubleOrNull()
        val isWeightValid = weight.isNotEmpty() && weightAsDouble != null && weightAsDouble > 0.0
        val isWeightValidForTextInput = weight.isEmpty() || isWeightValid // we don't show error until we have one character
        _inputState.update {
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

    private fun updateBirthDate(birthDate: String) {
        _inputState.update {
            it.copy(
                birthDate = birthDate,
                isBirthDateValid = true,
                inputRequirements = it.inputRequirements.plus(DogInputRequirements.BIRTH_DATE)
            )
        }
    }

    private fun updateBirthDateDialogShown() {
        _inputState.update {
            it.copy(
                isBirthDateValid = it.birthDate.isNotEmpty()
            )
        }
    }

    private fun saveDogInfo() {
        viewModelScope.launch(Dispatchers.Default) {
            // room will autogenerate id
            val dogInput = DogInput(
                profilePic = inputState.value.profilePic!!,
                name = inputState.value.name,
                weight = inputState.value.weight,
                birthDate = inputState.value.birthDate
            )
            addDogUseCase(dogInput)
        }
    }
}
