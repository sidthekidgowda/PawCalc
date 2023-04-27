package com.sidgowda.pawcalc.doglist.model

import com.sidgowda.pawcalc.data.dogs.model.Dog

sealed class DogListEvent {
    object FetchDogs : DogListEvent()
    object AddDog : DogListEvent()

    object OnNavigated : DogListEvent()
    object DogDetails : DogListEvent()
    data class DeleteDog(val dog: Dog) : DogListEvent()
}
