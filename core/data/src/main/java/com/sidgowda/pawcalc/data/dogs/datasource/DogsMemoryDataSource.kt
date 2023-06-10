package com.sidgowda.pawcalc.data.dogs.datasource

import com.sidgowda.pawcalc.data.date.dateToNewFormat
import com.sidgowda.pawcalc.data.dogs.mapInPlace
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.data.dogs.update
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class DogsMemoryDataSource @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    @Named("computationScope") scope: CoroutineScope
) : DogsDataSource {

    private val dogs = MutableStateFlow<List<Dog>>(emptyList())

    init {
        scope.launch {
            settingsDataSource.settings().collectLatest { settings ->
                dogs.update { oldDogs ->
                    // transform current list of dogs any time settings is updated
                    Timber.d("Transforming dogs with current settings")
                    oldDogs.transformWithSettings(settings)
                }
            }
        }
    }

    override fun dogs(): Flow<List<Dog>> {
        return dogs.asStateFlow()
    }

    private fun List<Dog>.transformWithSettings(settings: Settings): List<Dog> {
        return map { dog ->
            // if date format or weight format does not match -> convert
            val date = if (settings.dateFormat != dog.dateFormat) {
                Timber.d("Date format changed. Old Date Format: ${dog.dateFormat}, New Date Format: ${settings.dateFormat}")
                dog.birthDate.dateToNewFormat(settings.dateFormat)
            } else {
                dog.birthDate
            }
            val weight = if (settings.weightFormat != dog.weightFormat) {
                Timber.d("Weight format changed. Old Weight Format: ${dog.weightFormat}, New Weight Format: ${settings.weightFormat}")
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
        Timber.d("Adding Dog to memory")
        dogs.update { list ->
            list.update {
                it.addAll(dog)
            }
        }
    }

    override suspend fun deleteDog(dog: Dog) {
        Timber.d("Deleting Dog from memory")
       dogs.update { list ->
           list.update {
               it.remove(dog)
           }
       }
    }

    override suspend fun updateDogs(vararg dog: Dog) {
        Timber.d("Updating Dog in memory")
        val updatedDogIdsMap: Map<Int, Dog> = dog.associateBy { it.id }
        dogs.update { list ->
            list.update {
                it.mapInPlace { oldDog -> updatedDogIdsMap[oldDog.id] ?: oldDog }
            }
        }
    }

    override suspend fun clear() {
        Timber.d("Deleting all Dogs from memory")
        dogs.update { list ->
            list.update { it.clear() }
        }
    }
}
