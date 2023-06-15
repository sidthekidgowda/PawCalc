package com.sidgowda.pawcalc.data.dogs.model

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.dateToNewFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.db.dog.DogEntity


fun DogEntity.toDog(): Dog {
    return Dog(
        id = id,
        profilePic = profilePic,
        name = name,
        weightFormat = weightFormat,
        weightInKg = if (weightFormat == WeightFormat.KILOGRAMS) weight else weight.toNewWeight(
            WeightFormat.KILOGRAMS
        ).formattedToTwoDecimals(),
        weightInLb = if (weightFormat == WeightFormat.POUNDS) weight else weight.toNewWeight(
            WeightFormat.POUNDS
        ).formattedToTwoDecimals(),
        dateFormat = dateFormat,
        birthDateAmerican = if (dateFormat == DateFormat.AMERICAN) birthDate else birthDate.dateToNewFormat(DateFormat.AMERICAN),
        birthDateInternational = if (dateFormat == DateFormat.INTERNATIONAL) birthDate else birthDate.dateToNewFormat(DateFormat.INTERNATIONAL),
        dogYears = birthDate.toDogYears(dateFormat = dateFormat),
        humanYears = birthDate.toHumanYears(dateFormat = dateFormat),
        shouldAnimate = true
    )
}

fun Dog.toDogEntity(): DogEntity {
    return DogEntity(
        id = id,
        profilePic = profilePic,
        name = name,
        weight = if (weightFormat == WeightFormat.POUNDS) weightInLb else weightInKg,
        weightFormat = weightFormat,
        birthDate = if (dateFormat == DateFormat.AMERICAN) birthDateAmerican else birthDateInternational,
        dateFormat = dateFormat
    )
}
