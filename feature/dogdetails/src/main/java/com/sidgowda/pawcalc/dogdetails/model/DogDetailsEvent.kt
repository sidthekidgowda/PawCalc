package com.sidgowda.pawcalc.dogdetails.model

sealed class DogDetailsEvent {

    data class FetchDogForId(val id: Int) : DogDetailsEvent()

    data class EditDog(val id: Int) : DogDetailsEvent()

    object OnNavigated : DogDetailsEvent()

    object StartAnimation : DogDetailsEvent()

    object EndAnimation : DogDetailsEvent()
}
