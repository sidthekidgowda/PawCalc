package com.sidgowda.pawcalc.domain

import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
class GetDogForIdUseCase @Inject constructor(
    private val dogsRepo: DogsRepo
) {
    operator fun invoke(id: Int): Flow<Dog> {
        return dogsRepo.dogState()
            .map { it.dogs }
            .flatMapConcat { it.asFlow() }
            .filter { dog -> dog.id == id }
            .flowOn(Dispatchers.Default)
    }
}
