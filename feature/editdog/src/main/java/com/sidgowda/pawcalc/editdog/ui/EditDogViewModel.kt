package com.sidgowda.pawcalc.editdog.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.domain.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.UpdateDogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDogViewModel @Inject constructor(
    private val getDogForIdUseCase: GetDogForIdUseCase,
    private val updateDogUseCase: UpdateDogUseCase
) : ViewModel() {

    private val _inputState = MutableStateFlow(DogInputState(isLoading = true))
    val dogInputState = _inputState.asStateFlow()

    // this is the Dog we will be editing
    private val editableDog = MutableStateFlow<Dog?>(null)
    suspend fun fetchDogForId(id: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val dog = getDogForIdUseCase(id).first()
            editableDog.update { dog }
            _inputState.update {
                it.copy(
                    isLoading = false,
                    profilePic = dog.profilePic,
                    name = dog.name,
                    weight = dog.weight.toString(),
                    birthDate = dog.birthDate,
                    inputRequirements = DogInputRequirements.values().toSet()
                )
            }
        }
    }

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

    private fun updatePicture(pictureUrl: Uri?) {
        _inputState.update {
            it.copy(
                profilePic = pictureUrl,
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
        //do validations
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
            editableDog.updateAndGet { dog ->
                val input = _inputState.value
                dog?.copy(
                    name = input.name,
                    weight = input.weight.toDouble(),
                    birthDate = input.birthDate,
                    profilePic = input.profilePic!!
                )
            }?.let {
                updateDogUseCase(
                    it
                )
            }
        }
    }
}
