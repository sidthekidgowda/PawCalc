package com.sidgowda.pawcalc.data.dogs.datasource

import com.sidgowda.pawcalc.data.dogs.model.Dog
import kotlinx.coroutines.flow.Flow

interface DogsDataSource {

    fun dogs(): Flow<List<Dog>>

    suspend fun addDogs(vararg dog: Dog)

    suspend fun deleteDog(dog: Dog)

    suspend fun updateDog(vararg dog: Dog)

    suspend fun clear()
}
