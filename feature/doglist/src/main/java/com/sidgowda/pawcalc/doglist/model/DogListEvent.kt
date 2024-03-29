package com.sidgowda.pawcalc.doglist.model

import com.sidgowda.pawcalc.data.dogs.model.Dog

sealed class DogListEvent {

    object AddDog : DogListEvent()
    data class DogDetails(val id: Int) : DogListEvent()
    data class DeleteDog(val dog: Dog) : DogListEvent()

    object OnNavigated : DogListEvent()
}
