package com.sidgowda.pawcalc.editdog.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.dateToNewFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.formattedToString
import com.sidgowda.pawcalc.data.dogs.model.formattedToTwoDecimals
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.doginput.*
import com.sidgowda.pawcalc.doginput.model.*
import com.sidgowda.pawcalc.domain.dogs.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.dogs.UpdateDogUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import com.sidgowda.pawcalc.editdog.DOG_ID_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class EditDogViewModel @Inject constructor(
    private val getDogForIdUseCase: GetDogForIdUseCase,
    private val updateDogUseCase: UpdateDogUseCase,
    private val settingsUseCase: GetSettingsUseCase,
    savedStateHandle: SavedStateHandle,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
    @Named("computation") private val computationDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _inputState = MutableStateFlow(DogInputState())
    val dogInputState = _inputState.asStateFlow()

    // this is the Dog we will be editing
    private val editableDog = MutableStateFlow<Dog?>(null)

    private val dogId: Int = checkNotNull(savedStateHandle[DOG_ID_KEY])

    init {
        syncInputWithSettings()
        fetchDogForId()
    }

    private fun syncInputWithSettings() {
        // Collect from settings and update date and weight any time settings is updated
        viewModelScope.launch(ioDispatcher) {
            settingsUseCase().collectLatest { settings ->
                _inputState.update { currentInput ->
                    currentInput.copy(
                        birthDate = if (
                            currentInput.dateFormat != settings.dateFormat &&
                            currentInput.birthDate.isNotEmpty()
                        ) {
                            Timber.d("Date format changed to ${settings.dateFormat}. Date input is updated")
                            currentInput.birthDate.dateToNewFormat(settings.dateFormat)
                        } else {
                            currentInput.birthDate
                        },
                        weight = if (
                            currentInput.weightFormat != settings.weightFormat &&
                            currentInput.weight.isNotEmpty() && currentInput.weight.toDoubleOrNull() != null
                        ) {
                            Timber.d("Weight format changed to ${settings.weightFormat}. Weight input is updated")
                            currentInput.weight
                                .toDouble()
                                .toNewWeight(settings.weightFormat)
                                .formattedToString()
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

    private fun fetchDogForId() {
        viewModelScope.launch(computationDispatcher) {
            val dog = getDogForIdUseCase(dogId).first()
            _inputState.update {
                it.copy(
                    profilePic = dog.profilePic,
                    name = dog.name,
                    weight = if (dog.weightFormat == WeightFormat.POUNDS) dog.weightInLb.toString() else dog.weightInKg.toString(),
                    weightFormat = dog.weightFormat,
                    birthDate = if (dog.dateFormat == DateFormat.AMERICAN) dog.birthDateAmerican else dog.birthDateInternational,
                    dateFormat = dog.dateFormat,
                    inputRequirements = DogInputRequirements.values().toSet()
                )
            }.also {
                // cache the value of the dog
                Timber.d("Cached editable dog")
                editableDog.update { dog }
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
            Timber.d("Updating dog")
            editableDog.updateAndGet { oldDog ->
                val input = _inputState.value
                oldDog?.copy(
                    name = input.name,
                    weightFormat = input.weightFormat,
                    weightInKg = if (input.weightFormat == WeightFormat.KILOGRAMS) {
                        input.weight.toDouble().formattedToTwoDecimals()
                    } else {
                        input.weight.toDouble().toNewWeight(WeightFormat.KILOGRAMS)
                    },
                    weightInLb = if (input.weightFormat == WeightFormat.POUNDS) {
                        input.weight.toDouble().formattedToTwoDecimals()
                    } else {
                        input.weight.toDouble().toNewWeight(WeightFormat.POUNDS)
                    },
                    birthDateAmerican = if (input.dateFormat == DateFormat.AMERICAN) {
                        input.birthDate
                    } else {
                        input.birthDate.dateToNewFormat(DateFormat.AMERICAN)
                    },
                    birthDateInternational = if (input.dateFormat == DateFormat.INTERNATIONAL) {
                        input.birthDate
                    } else {
                        input.birthDate.dateToNewFormat(DateFormat.INTERNATIONAL)
                    },
                    dateFormat = input.dateFormat,
                    profilePic = input.profilePic!!,
                    dogYears = input.birthDate.toDogYears(dateFormat = input.dateFormat),
                    humanYears = input.birthDate.toHumanYears(dateFormat = input.dateFormat),
                    // if age changed, we should animate
                    shouldAnimate = oldDog.dogYears != input.birthDate.toDogYears(dateFormat = input.dateFormat)
                )
            }?.let { updatedDog ->
                updateDogUseCase(updatedDog)
            }
        }
    }
}
