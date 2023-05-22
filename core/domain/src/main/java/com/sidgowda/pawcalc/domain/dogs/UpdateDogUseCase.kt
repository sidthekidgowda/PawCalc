package com.sidgowda.pawcalc.domain.dogs

import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import javax.inject.Inject

class UpdateDogUseCase @Inject constructor(
    private val dogsRepo: DogsRepo
) {
    suspend operator fun invoke(dog: Dog) {
        dogsRepo.updateDog(dog)
    }
}
