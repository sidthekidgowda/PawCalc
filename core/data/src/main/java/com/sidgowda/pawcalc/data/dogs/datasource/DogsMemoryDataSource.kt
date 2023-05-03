package com.sidgowda.pawcalc.data.dogs.datasource

import com.sidgowda.pawcalc.data.dogs.mapInPlace
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.data.dogs.update
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.date.dateToNewFormat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class DogsMemoryDataSource @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val computationDispatcher: CoroutineDispatcher
) : DogsDataSource {

    private val dogs = MutableStateFlow<List<Dog>>(emptyList())

    override fun dogs(): Flow<List<Dog>> {
        // transform current list of dogs any time settings is updated
        return combine(dogs.asStateFlow(), settingsDataSource.settings()) { dogs, settings ->
            dogs.transformWithSettings(settings)
        }.flowOn(computationDispatcher)
    }

    private fun List<Dog>.transformWithSettings(settings: Settings): List<Dog> {
        return map { dog ->
            // if date format or weight format does not match -> convert
            val date = if (settings.dateFormat != dog.dateFormat) {
                dog.birthDate.dateToNewFormat(settings.dateFormat)
            } else {
                dog.birthDate
            }
            val weight = if (settings.weightFormat != dog.weightFormat) {
                dog.weight.toNewWeight(settings.weightFormat)
            } else {
                dog.weight
            }
            dog.copy(
                birthDate = date,
                dateFormat = settings.dateFormat,
                weight = weight,
                weightFormat = settings.weightFormat
            )
        }
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
