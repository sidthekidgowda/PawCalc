package com.sidgowda.pawcalc.data.dogs.model

import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.db.dog.DogEntity
import java.text.DecimalFormat

private const val ONE_KG_TO_LB = 2.20462
private const val ONE_LB_TO_KG = 0.45392
private const val WEIGHT_FORMAT = "#.##"

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

fun Double.toNewWeight(weightFormat: WeightFormat): Double {
    return when (weightFormat) {
        WeightFormat.POUNDS -> {
            // current weight is in kilograms, convert to pounds
            this * ONE_KG_TO_LB

        }
        WeightFormat.KILOGRAMS -> {
            // current weight is in pounds, convert to kilograms
            this * ONE_LB_TO_KG
        }
    }
}

fun weightFormattedToString(weight: Double): String {
    //DecimalFormat formater = new DecimalFormat("#.##");
    val formatter = DecimalFormat(WEIGHT_FORMAT)
    return formatter.format(weight)
}
