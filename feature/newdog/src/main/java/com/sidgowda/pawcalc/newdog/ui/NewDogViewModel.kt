package com.sidgowda.pawcalc.newdog.ui

import androidx.lifecycle.ViewModel
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NewDogViewModel @Inject constructor(

) : ViewModel() {

    private val _inputState = MutableStateFlow(DogInputState())
    val inputState = _inputState.asStateFlow()


    fun handleEvent(dogInputEvent: DogInputEvent) {
        when (dogInputEvent) {
            is DogInputEvent.PicChanged -> updatePicture(dogInputEvent.pictureUrl)
            is DogInputEvent.NameChanged -> updateName(dogInputEvent.name)
            is DogInputEvent.WeightChanged -> updateWeight(dogInputEvent.weight)
            is DogInputEvent.BirthDateChanged -> updateBirthDate(dogInputEvent.birthDate)
            is DogInputEvent.ErrorDismissed -> dismissError()
            is DogInputEvent.SavingInfo -> saveDogInfo()
        }
    }

    private fun updatePicture(url: String) {
        _inputState.update {
            it.copy(profilePicInput = url)
        }
    }

    private fun updateName(name: String) {
        // do validations
        _inputState.update {
            it.copy(nameInput = name)
        }
    }

    private fun updateBirthDate(birthDate: String) {
        //do validations
        _inputState.update {
            it.copy(birthDateInput = birthDate)
        }
    }

    private fun updateWeight(weight: String) {
        // do validations
        _inputState.update {
            it.copy(weightInput = weight)
        }
    }

    private fun dismissError() {
        _inputState.update {
            it.copy(isError = false)
        }
    }

    private fun saveDogInfo() {
        // save use case here if all requirements are met and close screen
        // otherwise showcase error and show what requirements are met
        if (_inputState.value.inputRequirements == DogInputRequirements.values().toList()) {

        } else {
            _inputState.update {
                it.copy(isError = true)
            }
        }

    }
}
