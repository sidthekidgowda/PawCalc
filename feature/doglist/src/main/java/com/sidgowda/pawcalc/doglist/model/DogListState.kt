package com.sidgowda.pawcalc.doglist.model

import com.sidgowda.pawcalc.data.dogs.model.Dog

data class DogListState(
    val isLoading: Boolean = true,
    val dogs: List<Dog> = emptyList(),
    val isError: Boolean = false
)
