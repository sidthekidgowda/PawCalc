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

    val onboardingState: Flow<OnboardingState> = getOnboardingState()

    private val _navigateEventFlow = MutableSharedFlow<NavigateEvent>(replay = 0)
    val navigateEventFlow = _navigateEventFlow.asSharedFlow()

    private val cachedDogList = MutableStateFlow<List<Dog>>(emptyList())

    val dogListState: StateFlow<DogListState> = dogsRepo.dogState()
        .map {
            DogListState(
                isLoading = it.isLoading,
                dogs = it.dogs
            )
        }
        .onEach { dogListState ->
            // update cache
            cachedDogList.update {
                dogListState.dogs
            }
        }
        .catch {
            // if an error is found upstream, use cached list of dogs
            emit(
                DogListState(
                    isLoading = false,
                    dogs = cachedDogList.value
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
            is DogListEvent.AddDog -> onNavigate(NavigateEvent.AddDog)
            is DogListEvent.DogDetails -> onNavigate(NavigateEvent.DogDetails(event.id))
            is DogListEvent.DeleteDog -> deleteDog(event.dog)
        }
    }

    private fun onNavigate(navigateEvent: NavigateEvent) {
        viewModelScope.launch {
            when (navigateEvent) {
                NavigateEvent.AddDog -> _navigateEventFlow.emit(NavigateEvent.AddDog)
                is NavigateEvent.DogDetails -> _navigateEventFlow.emit(
                    NavigateEvent.DogDetails(id = navigateEvent.id)
                )
            }
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
}
