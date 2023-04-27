package com.sidgowda.pawcalc.doglist.model

sealed class NavigateEvent {

    object AddDog : NavigateEvent()
    data class DogDetails(val id: Int) : NavigateEvent()
}
