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
        weightFormat = weightFormat,
        birthDate = birthDate,
        dateFormat = dateFormat,
        dogYears = birthDate.toDogYears(dateFormat = dateFormat),
        humanYears = birthDate.toHumanYears(dateFormat = dateFormat)
    )
}

fun Dog.toDogEntity(): DogEntity {
    return DogEntity(
        id = id,
        profilePic = profilePic,
        name = name,
        weight = weight,
        weightFormat = weightFormat,
        birthDate = birthDate,
        dateFormat = dateFormat
    )
}
