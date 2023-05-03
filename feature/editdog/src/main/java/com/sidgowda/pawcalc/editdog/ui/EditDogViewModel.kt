package com.sidgowda.pawcalc.editdog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.date.dateToNewFormat
import com.sidgowda.pawcalc.doginput.*
import com.sidgowda.pawcalc.doginput.model.*
import com.sidgowda.pawcalc.domain.dogs.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.dogs.UpdateDogUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class EditDogViewModel @Inject constructor(
    private val getDogForIdUseCase: GetDogForIdUseCase,
    private val updateDogUseCase: UpdateDogUseCase,
    private val settingsUseCase: GetSettingsUseCase,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
    @Named("computation") private val computationDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _inputState = MutableStateFlow(DogInputState(isLoading = true))
    val dogInputState = _inputState.asStateFlow()

    // this is the Dog we will be editing
    private val editableDog = MutableStateFlow<Dog?>(null)
    init {
        // Collect from settings and update date and weight any time settings is updated
        viewModelScope.launch(ioDispatcher) {
            settingsUseCase().collect { settings ->
                _inputState.update { currentInput ->
                    currentInput.copy(
                        birthDate = if (
                            currentInput.dateFormat != settings.dateFormat &&
                            currentInput.birthDate.isNotEmpty()
                        ) {
                            currentInput.birthDate.dateToNewFormat(settings.dateFormat)
                        } else {
                            currentInput.birthDate
                        },
                        weight = if (
                            currentInput.weightFormat != settings.weightFormat &&
                            currentInput.weight.isNotEmpty() && currentInput.isWeightValid
                        ) {
                            currentInput.weight
                                .toDouble()
                                .toNewWeight(settings.weightFormat)
                                .toString()
                        } else {
                            currentInput.weight
                        },
                        weightFormat = settings.weightFormat,
                        dateFormat = settings.dateFormat
                    )
                }
            }
        }
    }

    fun fetchDogForId(id: Int) {
        viewModelScope.launch(computationDispatcher) {
            try {
                // this will have dog with current settings format
                val dog = getDogForIdUseCase(id).first()
                _inputState.update {
                    it.copy(
                        isLoading = false,
                        profilePic = dog.profilePic,
                        name = dog.name,
                        weight = dog.weight.toString(),
                        weightFormat = dog.weightFormat,
                        birthDate = dog.birthDate,
                        dateFormat = dog.dateFormat,
                        inputRequirements = DogInputRequirements.values().toSet()
                    )
                }.also {
                    // cache the value of the dog
                    editableDog.update { dog }
                }
            } catch (e: Exception) {
                // add logs
                _inputState.update {
                    it.copy(
                        isLoading = false,
                        isError = true
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
            editableDog.updateAndGet { oldDog ->
                val input = _inputState.value
                oldDog?.copy(
                    name = input.name,
                    weight = input.weight.toDouble().toNewWeight(input.weightFormat),
                    weightFormat = input.weightFormat,
                    birthDate = input.birthDate,
                    dateFormat = input.dateFormat,
                    profilePic = input.profilePic!!,
                    dogYears = input.birthDate.toDogYears(dateFormat = input.dateFormat),
                    humanYears = input.birthDate.toHumanYears(dateFormat = input.dateFormat)
                )
            }?.let { updatedDog ->
                updateDogUseCase(updatedDog)
            }
        }
    }
}
