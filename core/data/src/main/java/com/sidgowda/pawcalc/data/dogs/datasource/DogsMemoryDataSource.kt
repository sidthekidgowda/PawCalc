package com.sidgowda.pawcalc.data.dogs.datasource

import com.sidgowda.pawcalc.data.dogs.mapInPlace
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.update
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
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
                    // update date and weight format any time settings is updated
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
            // update date format or weight format
            dog.copy(
                dateFormat = settings.dateFormat,
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
