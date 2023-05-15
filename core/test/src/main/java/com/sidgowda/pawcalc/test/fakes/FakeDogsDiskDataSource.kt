package com.sidgowda.pawcalc.test.fakes

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.mapInPlace
import com.sidgowda.pawcalc.data.dogs.model.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDogsDiskDataSource(dogs: List<Dog> = emptyList()) : DogsDataSource {

    val listOfDogs = dogs.toMutableList()

    override fun dogs(): Flow<List<Dog>> {
        return flow {
            emit(listOfDogs)
        }
    }

    override suspend fun addDogs(vararg dog: Dog) {
        listOfDogs.addAll(dog)
    }

    override suspend fun deleteDog(dog: Dog) {
        listOfDogs.remove(dog)
    }

    override suspend fun updateDogs(vararg dog: Dog) {
        val updatedDogIdsMap: Map<Int, Dog> = dog.associateBy { it.id }
        listOfDogs.mapInPlace { oldDog -> updatedDogIdsMap[oldDog.id] ?: oldDog }
    }

    override suspend fun clear() {
        listOfDogs.clear()
    }
}

