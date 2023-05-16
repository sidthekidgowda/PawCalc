package com.sidgowda.pawcalc.dogdetails.model

sealed class DogDetailsEvent {

    object EditDog : DogDetailsEvent()

    object OnNavigated : DogDetailsEvent()

    object OnAnimationFinished : DogDetailsEvent()
}
