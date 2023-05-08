package com.sidgowda.pawcalc.dogdetails.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.dogs.model.throttleFirst
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.date.dateToNewFormat
import com.sidgowda.pawcalc.dogdetails.DOG_ID_KEY
import com.sidgowda.pawcalc.dogdetails.model.DogDetailsEvent
import com.sidgowda.pawcalc.dogdetails.model.DogDetailsState
import com.sidgowda.pawcalc.dogdetails.model.NavigateEvent
import com.sidgowda.pawcalc.domain.dogs.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class DogDetailsViewModel @Inject constructor(
    private val getDogForIdUseCase: GetDogForIdUseCase,
    private val settingsUseCase: GetSettingsUseCase,
    savedStateHandle: SavedStateHandle,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
    @Named("computation") private val computationDispatcher: CoroutineDispatcher
) : ViewModel() {

    companion object {
        private const val THROTTLE_DURATION = 300L
    }

    private val navigateEventFlow = MutableSharedFlow<NavigateEvent>(replay = 0)

    private val _dogDetailsState = MutableStateFlow(DogDetailsState())
    val dogDetailsState: StateFlow<DogDetailsState> = _dogDetailsState.asStateFlow()

    private val dogId: Int = checkNotNull(savedStateHandle[DOG_ID_KEY])

    init {
        fetchDogForId()
        syncDetailsWithSettings()
        handleNavigateEvents()
    }

    private fun fetchDogForId() {
        viewModelScope.launch(computationDispatcher) {
            // if user has edited dog, we need to collect to get most recent updates on the dog
            getDogForIdUseCase(id = dogId).collect { dog ->
                _dogDetailsState.update {
                    it.copy(dog = dog)
                }
            }
        }
    }

    private fun syncDetailsWithSettings() {
        // Collect from settings and update date and weight any time settings is updated
        viewModelScope.launch(ioDispatcher) {
            settingsUseCase().collect { settings ->
                _dogDetailsState.update { details ->
                    details.copy(
                        dog = details.dog?.copy(
                            birthDate = if (
                                details.dog.dateFormat != settings.dateFormat &&
                                details.dog.birthDate.isNotEmpty()
                            ) {
                                details.dog.birthDate.dateToNewFormat(settings.dateFormat)
                            } else {
                                details.dog.birthDate
                            },
                            weight = if (details.dog.weightFormat != settings.weightFormat) {
                                details.dog.weight.toNewWeight(settings.weightFormat)
                            } else {
                                details.dog.weight
                            },
                            weightFormat = settings.weightFormat,
                            dateFormat = settings.dateFormat
                        )
                    )
                }
            }
        }
    }

    private fun handleNavigateEvents() {
        viewModelScope.launch {
            // taking first click event and reducing to ui state
            navigateEventFlow.throttleFirst(THROTTLE_DURATION).collect { navigateEvent ->
                _dogDetailsState.update {
                    it.copy(
                        navigateEvent = when (navigateEvent) {
                            is NavigateEvent.EditDog -> navigateEvent
                        }
                    )
                }
            }
        }
    }

    fun handleEvent(dogDetailsEvent: DogDetailsEvent) {
        when (dogDetailsEvent) {
            is DogDetailsEvent.EditDog -> navigate(NavigateEvent.EditDog(dogId))
            is DogDetailsEvent.StartAnimation -> {

            }
            is DogDetailsEvent.EndAnimation -> {

            }
            is DogDetailsEvent.OnNavigated -> _dogDetailsState.update {
                it.copy(navigateEvent = null)
            }
        }
    }

    private fun navigate(navigateEvent: NavigateEvent) {
        viewModelScope.launch {
            navigateEventFlow.emit(navigateEvent)
        }
    }
}
