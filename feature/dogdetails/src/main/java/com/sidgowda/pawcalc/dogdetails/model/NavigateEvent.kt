package com.sidgowda.pawcalc.dogdetails.model

sealed class NavigateEvent {
    data class EditDog(val dogId: Int) : NavigateEvent()
}
