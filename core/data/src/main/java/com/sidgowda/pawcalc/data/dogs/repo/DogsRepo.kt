package com.sidgowda.pawcalc.data.dogs.repo

import com.sidgowda.pawcalc.data.dogs.di.DogState
import com.sidgowda.pawcalc.data.dogs.model.Dog
import kotlinx.coroutines.flow.Flow

interface DogsRepo {

    fun dogState(): Flow<DogState>

    suspend fun fetchDogs()

    suspend fun addDog(dog: Dog)

    suspend fun deleteDog(dog: Dog)

    suspend fun updateDog(dog: Dog)

}
