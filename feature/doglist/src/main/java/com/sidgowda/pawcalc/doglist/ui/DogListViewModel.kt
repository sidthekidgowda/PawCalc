package com.sidgowda.pawcalc.doglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.doglist.model.DogListEvent
import com.sidgowda.pawcalc.doglist.model.DogListState
import com.sidgowda.pawcalc.doglist.model.NavigateEvent
import com.sidgowda.pawcalc.domain.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase,
    private val dogsRepo: DogsRepo
) : ViewModel() {

    data class LocalState(
        val navigateEvent: NavigateEvent?,
        val cachedDogs: List<Dog>
    )

    val onboardingState: Flow<OnboardingState> = getOnboardingState()

    private val _localDogListState = MutableStateFlow(
        LocalState(
            navigateEvent = null,
            cachedDogs = emptyList()
        )
    )

    val dogListState: StateFlow<DogListState> =
        combine(_localDogListState, dogsRepo.dogState()) { localState, repoState ->
            DogListState(
                isLoading = repoState.isLoading,
                dogs = repoState.dogs,
                navigateEvent = localState.navigateEvent
            )
        }
            .onEach { dogListState ->
                // update cache
                _localDogListState.update {
                    it.copy(cachedDogs = dogListState.dogs)
                }
            }
            .catch {
                // if an error is found upstream, use cached list of dogs
                emit(
                    DogListState(
                        isLoading = false,
                        dogs = _localDogListState.value.cachedDogs
                    )
                )
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = DogListState()
            )

    fun handleEvent(event: DogListEvent) {
        when (event) {
            is DogListEvent.FetchDogs -> fetchDogs()
            is DogListEvent.AddDog -> addDog()
            is DogListEvent.DogDetails -> dogDetails()
            is DogListEvent.DeleteDog -> deleteDog(event.dog)
            is DogListEvent.OnNavigated -> onNavigated()
        }
    }

    private fun addDog() {
        _localDogListState.update {
            it.copy(navigateEvent = NavigateEvent.ADD_DOG)
        }
    }

    private fun dogDetails() {
        _localDogListState.update {
            it.copy(navigateEvent = NavigateEvent.DOG_DETAILS)
        }
    }

    private fun fetchDogs() {
        viewModelScope.launch(Dispatchers.IO) {
            dogsRepo.fetchDogs()
        }
    }

    private fun deleteDog(dog: Dog) {
        viewModelScope.launch(Dispatchers.IO) {
            dogsRepo.deleteDog(dog)
        }
    }

    private fun onNavigated() {
        _localDogListState.update {
            it.copy(navigateEvent = null)
        }
    }
}
