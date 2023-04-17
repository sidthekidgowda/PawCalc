package com.sidgowda.pawcalc.editdog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.doginput.*
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.domain.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.UpdateDogUseCase
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
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
    @Named("computation") private val computationDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _inputState = MutableStateFlow(DogInputState(isLoading = true))
    val dogInputState = _inputState.asStateFlow()

    // this is the Dog we will be editing
    private val editableDog = MutableStateFlow<Dog?>(null)
    suspend fun fetchDogForId(id: Int) {
        viewModelScope.launch(computationDispatcher) {
            val dog = getDogForIdUseCase(id).first()
            _inputState.update {
                it.copy(
                    isLoading = false,
                    profilePic = dog.profilePic,
                    name = dog.name,
                    weight = dog.weight.toString(),
                    birthDate = dog.birthDate,
                    inputRequirements = DogInputRequirements.values().toSet()
                )
            }.also {
                // cache the value of the dog
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
            editableDog.updateAndGet { oldDog ->
                val input = _inputState.value
                oldDog?.copy(
                    name = input.name,
                    weight = input.weight.toDouble(),
                    birthDate = input.birthDate,
                    profilePic = input.profilePic!!
                )
            }?.let { updatedDog ->
                updateDogUseCase(
                    updatedDog
                )
            }
        }
    }
}
