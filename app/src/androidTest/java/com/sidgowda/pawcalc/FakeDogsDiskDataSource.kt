package com.sidgowda.pawcalc

import android.net.Uri
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.mapInPlace
import com.sidgowda.pawcalc.data.dogs.model.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDogsDiskDataSource : DogsDataSource {

    val listOfDogs = mutableListOf<Dog>(
        Dog(
            id = 1,
            name = "Dog_1",
            weight = 68.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/1/2021",
            dogYears = "12/1/2021".toDogYears(),
            humanYears = "12/1/2021".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        ), Dog(
            id = 2,
            name = "Dog_2",
            weight = 68.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/2/2021",
            dogYears = "12/2/2021".toDogYears(),
            humanYears = "12/2/2021".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        ), Dog(
            id = 3,
            name = "Dog_3",
            weight = 68.0,
            profilePic = Uri.EMPTY,
            birthDate = "12/3/2021",
            dogYears = "12/3/2021".toDogYears(),
            humanYears = "12/3/2021".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            shouldAnimate = true
        )
    )

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

