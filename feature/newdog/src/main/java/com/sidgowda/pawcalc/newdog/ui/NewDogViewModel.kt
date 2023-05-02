package com.sidgowda.pawcalc.newdog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.date.dateToNewFormat
import com.sidgowda.pawcalc.doginput.*
import com.sidgowda.pawcalc.doginput.model.*
import com.sidgowda.pawcalc.domain.dogs.AddDogUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class NewDogViewModel @Inject constructor(
    private val addDogUseCase: AddDogUseCase,
    private val settingsUseCase: GetSettingsUseCase,
    @Named("io") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _inputState = MutableStateFlow(DogInputState())
    val inputState = _inputState.asStateFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            settingsUseCase().collect { settings ->
                _inputState.update {
                    it.copy(
                        isLoading = false,
                        birthDate = if (
                            _inputState.value.dateFormat != settings.dateFormat &&
                            _inputState.value.birthDate.isNotEmpty()
                        ) {
                            _inputState.value.birthDate.dateToNewFormat(settings.dateFormat)
                        } else {
                            _inputState.value.birthDate
                        },
                        weightFormat = settings.weightFormat,
                        dateFormat = settings.dateFormat
                    )
                }
            }
        }
    }

    fun handleEvent(dogInputEvent: DogInputEvent) {
        when (dogInputEvent) {
            is DogInputEvent.PicChanged -> _inputState.updateProfilePic(dogInputEvent.pictureUrl)
            is DogInputEvent.NameChanged -> _inputState.updateName(dogInputEvent.name)
            is DogInputEvent.WeightChanged -> _inputState.updateWeight(dogInputEvent.weight)
            is DogInputEvent.BirthDateChanged -> _inputState.updateBirthDate(dogInputEvent.birthDate)
            is DogInputEvent.BirthDateDialogShown -> _inputState.updateBirthDateDialogShown()
            is DogInputEvent.SavingInfo -> saveDogInfo()
        }
    }

    private fun saveDogInfo() {
        viewModelScope.launch(ioDispatcher) {
            val dogInput = DogInput(
                profilePic = _inputState.value.profilePic!!,
                name = _inputState.value.name,
                weight = _inputState.value.weight,
                weightFormat = _inputState.value.weightFormat,
                birthDate = _inputState.value.birthDate,
                dateFormat = _inputState.value.dateFormat
            )
            addDogUseCase(dogInput)
        }
    }
}
