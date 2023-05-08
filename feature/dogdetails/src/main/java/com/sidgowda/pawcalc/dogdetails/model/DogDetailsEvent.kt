package com.sidgowda.pawcalc.dogdetails.model

sealed class DogDetailsEvent {

    object EditDog : DogDetailsEvent()

    object OnNavigated : DogDetailsEvent()

    object StartAnimation : DogDetailsEvent()

    object EndAnimation : DogDetailsEvent()
}
