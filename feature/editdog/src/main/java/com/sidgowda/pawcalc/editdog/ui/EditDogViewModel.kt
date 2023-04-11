package com.sidgowda.pawcalc.editdog.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class EditDogViewModel @Inject constructor() : ViewModel() {

    private val _dogInputState = MutableStateFlow(DogInputState())
    val dogInputState = _dogInputState.asStateFlow()

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

    private fun updateBirthDateDialogShown() {
        _dogInputState.update {
            it.copy(
                isBirthDateValid = it.isBirthDateValid
            )
        }
    }


    private fun updatePicture(pictureUrl: Uri?) {
        _dogInputState.update {
            it.copy(profilePic = pictureUrl)
        }
    }

    private fun updateName(name: String) {
        // do validations
        _dogInputState.update {
            it.copy(name = name)
        }
    }

    private fun updateBirthDate(birthDate: String) {
        //do validations
        _dogInputState.update {
            it.copy(birthDate = birthDate)
        }
    }

    private fun updateWeight(weight: String) {
        // do validations
        _dogInputState.update {
            it.copy(weight = weight)
        }
    }

    private fun saveDogInfo() {
        // save use case here if all requirements are met and close screen
        // otherwise showcase error and show what requirements are met

    }
}
