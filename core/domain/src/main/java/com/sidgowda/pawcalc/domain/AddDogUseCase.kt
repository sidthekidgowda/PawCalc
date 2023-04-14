package com.sidgowda.pawcalc.domain

import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import javax.inject.Inject

class AddDogUseCase @Inject constructor(
    private val dogsRepo: DogsRepo
) {
    suspend operator fun invoke(dogInput: DogInput) {
        dogsRepo.addDog(dogInput)
    }
}
