package com.sidgowda.pawcalc.data.dogs.di

import com.sidgowda.pawcalc.data.dogs.model.Dog

data class DogState(
    val isLoading: Boolean,
    val dogs: List<Dog>
)
