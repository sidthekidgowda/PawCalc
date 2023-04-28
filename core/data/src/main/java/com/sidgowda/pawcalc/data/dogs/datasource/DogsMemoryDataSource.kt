package com.sidgowda.pawcalc.data.dogs.datasource

import com.sidgowda.pawcalc.data.dogs.model.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class DogsMemoryDataSource @Inject constructor() : DogsDataSource {

    private val dogs = MutableStateFlow<List<Dog>?>(null)

    override fun dogs(): Flow<List<Dog>?> {
        return dogs.asStateFlow()
    }

    override suspend fun addDog(vararg dog: Dog) {
        dogs.update { list ->
            list?.update {
                it.addAll(dog)
            } ?: dog.asList()
        }
    }

    override suspend fun deleteDog(dog: Dog) {
       dogs.update { list ->
           list?.update {
               it.remove(dog)
           }
       }
    }

    override suspend fun updateDog(dog: Dog) {
        dogs.update { list ->
            list?.update {
                val indexToReplace = it.indexOfFirst { oldDog -> dog.id == oldDog.id }
                if (indexToReplace != -1) {
                    it[indexToReplace] = dog
                }
            }
        }
    }

    override suspend fun clear() {
        dogs.update {
            null
        }
    }

    private fun List<Dog>.update(action: (MutableList<Dog>) -> Unit): List<Dog> {
        return toMutableList().apply {
            action(this)
        }
    }
}
