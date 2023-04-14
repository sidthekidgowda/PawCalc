package com.sidgowda.pawcalc.doglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.doglist.model.DogListState
import com.sidgowda.pawcalc.domain.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase,
    private val dogsRepo: DogsRepo
) : ViewModel() {

    private val cachedDogList = mutableListOf<Dog>()
    private val mutex = Mutex()

    val onboardingState: Flow<OnboardingState> = getOnboardingState()

    val dogListState: StateFlow<DogListState> =
        dogsRepo.dogState().map {
            DogListState(
                isLoading = it.isLoading,
                dogs = it.dogs,
                isError = false
            )
        }
        .onEach {
            // update cache
            mutex.withLock {
                cachedDogList.clear()
                cachedDogList.addAll(it.dogs)
            }
        }
        .catch {
            // if an error is found upstream, use cached list of dogs
            emit(
                DogListState(
                    isLoading = false,
                    dogs = cachedDogList,
                    isError = true
                )
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DogListState()
        )

    fun fetchDogs() {
        viewModelScope.launch(Dispatchers.IO) {
            dogsRepo.fetchDogs()
        }
    }

    fun deleteDog(dog: Dog) {
        viewModelScope.launch(Dispatchers.IO) {
            dogsRepo.deleteDog(dog)
        }
    }
}
