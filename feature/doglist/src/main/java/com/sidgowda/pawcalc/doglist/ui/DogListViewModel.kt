package com.sidgowda.pawcalc.doglist.ui

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.throttleFirst
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.doglist.model.DogListEvent
import com.sidgowda.pawcalc.doglist.model.DogListState
import com.sidgowda.pawcalc.doglist.model.NavigateEvent
import com.sidgowda.pawcalc.domain.onboarding.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class DogListViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase,
    private val dogsRepo: DogsRepo,
    private val savedStateHandle: SavedStateHandle,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
    @Named("computation") private val computationDispatcher: CoroutineDispatcher
) : ViewModel() {

    companion object {
        private const val THROTTLE_DURATION = 300L
        private const val KEY_SAVED_LOCAL_STATE = "saved_dog_list_local_state"
    }

    @Parcelize
    data class LocalState(
        val navigateEvent: NavigateEvent?,
        val cachedDogs: List<Dog>
    ) : Parcelable

    val onboardingState: Flow<OnboardingState> = getOnboardingState()

    private val _navigateEventFlow = MutableSharedFlow<NavigateEvent>(replay = 0)

    private val localDogListState = MutableStateFlow(
        savedStateHandle.get(KEY_SAVED_LOCAL_STATE) ?: LocalState(
            navigateEvent = null,
            cachedDogs = emptyList()
        )
    )

    val dogListState: StateFlow<DogListState> =
        combine(localDogListState, dogsRepo.dogState()) { localState, repoState ->
            DogListState(
                isLoading = repoState.isLoading,
                dogs = repoState.dogs,
                navigateEvent = localState.navigateEvent
            )
        }
        .catch {
            // if an error is found upstream, use cached list of dogs
            emit(
                DogListState(
                    isLoading = false,
                    dogs = localDogListState.value.cachedDogs
                )
            )
        }
        .onEach { dogListState ->
            // update cache
            localDogListState.update {
                it.copy(cachedDogs = dogListState.dogs)
            }
        }
        .flowOn(computationDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DogListState()
        )

    init {
        fetchDogs()
        handleNavigateEvents()
        updateSavedStateHandle()
    }

    private fun fetchDogs() {
        viewModelScope.launch(ioDispatcher) {
            dogsRepo.fetchDogs()
        }
    }

    private fun handleNavigateEvents() {
        viewModelScope.launch {
            // taking the first click event and ignoring the rest
            _navigateEventFlow.throttleFirst(THROTTLE_DURATION).collect { navigateEvent ->
                // reduce to ui state
                when (navigateEvent) {
                    NavigateEvent.AddDog -> localDogListState.update {
                        it.copy(navigateEvent = NavigateEvent.AddDog)
                    }
                    is NavigateEvent.DogDetails -> localDogListState.update {
                        it.copy(
                            navigateEvent = NavigateEvent.DogDetails(id = navigateEvent.id)
                        )
                    }
                }
            }
        }
    }

    private fun updateSavedStateHandle() {
        viewModelScope.launch {
            localDogListState.collect {
                savedStateHandle[KEY_SAVED_LOCAL_STATE] = it
            }
        }
    }

    fun handleEvent(event: DogListEvent) {
        when (event) {
            is DogListEvent.AddDog -> onNavigate(NavigateEvent.AddDog)
            is DogListEvent.DogDetails -> onNavigate(NavigateEvent.DogDetails(event.id))
            is DogListEvent.DeleteDog -> deleteDog(event.dog)
            is DogListEvent.OnNavigated -> localDogListState.update { it.copy(navigateEvent = null) }
        }
    }

    private fun onNavigate(navigateEvent: NavigateEvent) {
        viewModelScope.launch {
            _navigateEventFlow.emit(navigateEvent)
        }
    }

    private fun deleteDog(dog: Dog) {
        viewModelScope.launch(ioDispatcher) {
            dogsRepo.deleteDog(dog)
        }
    }
}
