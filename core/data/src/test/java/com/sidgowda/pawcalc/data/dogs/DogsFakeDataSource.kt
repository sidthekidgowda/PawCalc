package com.sidgowda.pawcalc.data.dogs

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal object DogsFakeDataSource : DogsDataSource {
    val listOfDogs = mutableListOf<Dog>()

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

    override suspend fun updateDog(vararg dog: Dog) {
        if (dog.size == 1) {
            val indexToReplace = listOfDogs.indexOfFirst { oldDog -> dog.first().id == oldDog.id }
            if (indexToReplace != -1) {
                listOfDogs[indexToReplace] = dog.first()
            }
        }
    }

    override suspend fun clear() {
        listOfDogs.clear()
    }
}
