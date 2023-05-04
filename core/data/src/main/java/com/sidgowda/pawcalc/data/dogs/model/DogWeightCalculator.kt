package com.sidgowda.pawcalc.data.dogs.model

import com.sidgowda.pawcalc.common.settings.WeightFormat
import java.text.DecimalFormat

private const val ONE_KG_TO_LBS = 2.20462
private const val WEIGHT_FORMAT = "#.##"

fun Double.toNewWeight(newWeightFormat: WeightFormat): Double {
    val newWeight = when (newWeightFormat) {
        WeightFormat.POUNDS -> {
            // current weight is in kilograms, convert to pounds
            this * ONE_KG_TO_LBS

        }
        WeightFormat.KILOGRAMS -> {
            // current weight is in pounds, convert to kilograms
            this / ONE_KG_TO_LBS
        }
    }
    // round new weight to 2 digits
    return newWeight.formattedToTwoDecimals()
}

fun Double.formattedToTwoDecimals(): Double {
    return formattedToString().toDouble()
}

fun Double.formattedToString(): String {
    val formatter = DecimalFormat(WEIGHT_FORMAT)
    return formatter.format(this)
}
