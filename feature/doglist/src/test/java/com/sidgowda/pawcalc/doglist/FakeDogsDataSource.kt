package com.sidgowda.pawcalc.doglist

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.mapInPlace
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeDogsDataSource(dogList: List<Dog> = emptyList()) : DogsDataSource {

    private val dogs = MutableStateFlow(dogList)

    override fun dogs(): Flow<List<Dog>> {
        // transform current list of dogs any time settings is updated
        return dogs.asStateFlow()
    }

    override suspend fun addDogs(vararg dog: Dog) {
        dogs.update { list ->
            list.update {
                it.addAll(dog)
            }
        }
    }

    override suspend fun deleteDog(dog: Dog) {
        dogs.update { list ->
            list.update {
                it.remove(dog)
            }
        }
    }

    override suspend fun updateDogs(vararg dog: Dog) {
        val updatedDogIdsMap: Map<Int, Dog> = dog.associateBy { it.id }
        dogs.update { list ->
            list.update {
                it.mapInPlace { oldDog -> updatedDogIdsMap[oldDog.id] ?: oldDog }
            }
        }
    }

    override suspend fun clear() {
        dogs.update { list ->
            list.update { it.clear() }
        }
    }
}
