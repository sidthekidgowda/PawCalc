package com.sidgowda.pawcalc.data.dogs.model

import com.sidgowda.pawcalc.common.settings.WeightFormat
import java.text.DecimalFormat

private const val ONE_KG_TO_LBS = 2.20462
private const val ONE_LB_TO_KGS = 0.45392
private const val WEIGHT_FORMAT = "#.##"

fun Double.toNewWeight(weightFormat: WeightFormat): Double {
    return when (weightFormat) {
        WeightFormat.POUNDS -> {
            // current weight is in kilograms, convert to pounds
            this / ONE_KG_TO_LBS

        }
        WeightFormat.KILOGRAMS -> {
            // current weight is in pounds, convert to kilograms
            this * ONE_LB_TO_KGS
        }
    }
}

fun weightFormattedToString(weight: Double): String {
    val formatter = DecimalFormat(WEIGHT_FORMAT)
    return formatter.format(weight)
}
