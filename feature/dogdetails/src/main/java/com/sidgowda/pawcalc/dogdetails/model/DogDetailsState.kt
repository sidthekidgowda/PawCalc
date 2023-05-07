package com.sidgowda.pawcalc.dogdetails.model

import com.sidgowda.pawcalc.data.dogs.model.Dog

data class DogDetailsState(
    val isLoading: Boolean = true,
    val dog: Dog? = null,
    val navigateEvent: NavigateEvent? = null
)
