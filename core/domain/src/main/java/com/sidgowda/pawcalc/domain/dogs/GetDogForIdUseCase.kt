package com.sidgowda.pawcalc.domain.dogs

import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

@OptIn(FlowPreview::class)
class GetDogForIdUseCase @Inject constructor(
    private val dogsRepo: DogsRepo,
    @Named ("computation") private val computationDispatcher: CoroutineDispatcher
) {
    operator fun invoke(id: Int): Flow<Dog> {
        return dogsRepo.dogState()
            .filter { dogState -> !dogState.isLoading }
            .map { it.dogs }
            .flatMapConcat { it.asFlow() }
            .filter { dog -> dog.id == id }
            .flowOn(computationDispatcher)
    }
}
