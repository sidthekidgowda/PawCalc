package com.sidgowda.pawcalc.data.dogs.datasource

import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toDog
import com.sidgowda.pawcalc.data.dogs.model.toDogEntity
import com.sidgowda.pawcalc.db.dog.DogsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiskDogsDataSource @Inject constructor(
    private val dogsDao: DogsDao
) : DogsDataSource {

    override fun dogs(): Flow<List<Dog>?> {
        return dogsDao.dogs().map { list ->
            list.map {
                it.toDog(
                    dogYears = "",
                    humanYears = ""
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun addDog(vararg dog: Dog) {
        dog.forEach {
            dogsDao.addDog(it.toDogEntity())
        }
    }

    override suspend fun deleteDog(dog: Dog) {
        dogsDao.deleteDog(dog.toDogEntity())
    }

    override suspend fun updateDog(dog: Dog) {
        dogsDao.updateDog(dog.toDogEntity())
    }

}
