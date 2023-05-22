package com.sidgowda.pawcalc.data.dogs.datasource

import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toDog
import com.sidgowda.pawcalc.data.dogs.model.toDogEntity
import com.sidgowda.pawcalc.db.dog.DogsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class DogsDiskDataSource @Inject constructor(
    private val dogsDao: DogsDao
) : DogsDataSource {

    override fun dogs(): Flow<List<Dog>> {
        return dogsDao.dogs()
            .catch { exception ->
                if (exception is IOException) {
                    Timber.e(exception, "IO exception reading dogs from disk.")
                    emit(emptyList())
                } else {
                    Timber.e(exception, "Non IO exception reading dogs from disk.")
                    throw exception
                }
            }
            .map { list ->
                list.map {
                    it.toDog()
                }
            }
            .onEach {
                if (it.isNotEmpty()) {
                    Timber.d("Successfully retrieved dog list from disk")
                }
            }
            .flowOn(Dispatchers.Default)
    }

    override suspend fun addDogs(vararg dog: Dog) {
        Timber.d("Adding Dog to disk")
        dogsDao.addDog(*dog.map { it.toDogEntity() }.toTypedArray())
    }

    override suspend fun deleteDog(dog: Dog) {
        Timber.d("Deleting Dog from disk")
        dogsDao.deleteDog(dog.toDogEntity())
    }

    override suspend fun updateDogs(vararg dog: Dog) {
        Timber.d("Updating Dog in disk")
        val dogEntities = dog.map { it.toDogEntity() }
        dogsDao.updateDog(*dogEntities.toTypedArray())
    }

    override suspend fun clear() {
        Timber.d("Deleting all Dogs from disk")
        dogsDao.deleteAll()
    }
}
