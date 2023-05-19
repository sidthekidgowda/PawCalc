package com.sidgowda.pawcalc.domain.dogs

import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.model.Dog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@OptIn(FlowPreview::class)
class GetDogForIdUseCase @Inject constructor(
    @Named("memory") private val dogsDataSource: DogsDataSource,
    @Named ("computation") private val computationDispatcher: CoroutineDispatcher
) {
    operator fun invoke(id: Int): Flow<Dog> {
        return dogsDataSource.dogs()
            .flatMapConcat { it.asFlow() }
            .filter { dog -> dog.id == id }
            .onEach {
                Timber.d("id = $id, Dog id = ${it.id}")
            }
            .flowOn(computationDispatcher)
    }
}
