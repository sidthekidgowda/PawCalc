package com.sidgowda.pawcalc.data.dogs.model

import com.sidgowda.pawcalc.db.dog.DogEntity

fun DogEntity.toDog(dogYears: String, humanYears: String): Dog {
    return Dog(
        id = id,
        profilePic = profilePic,
        name = name,
        weight = weight,
        birthDate = birthDate,
        dogYears = dogYears,
        humanYears = humanYears,
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
