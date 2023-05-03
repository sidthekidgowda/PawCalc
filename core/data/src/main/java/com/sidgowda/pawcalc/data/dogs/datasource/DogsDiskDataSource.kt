package com.sidgowda.pawcalc.data.dogs.datasource

import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toDog
import com.sidgowda.pawcalc.data.dogs.model.toDogEntity
import com.sidgowda.pawcalc.db.dog.DogsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DogsDiskDataSource @Inject constructor(
    private val dogsDao: DogsDao
) : DogsDataSource {

    override fun dogs(): Flow<List<Dog>> {
        return dogsDao.dogs()
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyList())
                } else {
                    throw exception
                }
            }
            .map { list ->
                list.map {
                    it.toDog()
                }
            }.flowOn(Dispatchers.Default)
    }

    override suspend fun addDogs(vararg dog: Dog) {
        dog.forEach {
            dogsDao.addDog(it.toDogEntity())
        }
    }

    override suspend fun deleteDog(dog: Dog) {
        dogsDao.deleteDog(dog.toDogEntity())
    }

    override suspend fun updateDogs(vararg dog: Dog) {
        val dogEntitities = dog.map { it.toDogEntity() }
        dogsDao.updateDog(*dogEntitities.toTypedArray())
    }

    override suspend fun clear() {
        dogsDao.deleteAll()
    }
}
