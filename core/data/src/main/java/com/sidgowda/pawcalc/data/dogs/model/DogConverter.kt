package com.sidgowda.pawcalc.data.dogs.model

import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.db.dog.DogEntity

fun DogEntity.toDog(): Dog {
    return Dog(
        id = id,
        profilePic = profilePic,
        name = name,
        weight = weight,
        birthDate = birthDate,
        dogYears = birthDate.toDogYears(),
        humanYears = birthDate.toHumanYears(),
        isLoading = false
    )
}

fun Dog.toDogEntity(): DogEntity {
    return DogEntity(
        id = id,
        profilePic = profilePic,
        name = name,
        weight = weight,
        birthDate = birthDate
    )
}
