package com.sidgowda.pawcalc.dogdetails.ui

import com.sidgowda.pawcalc.data.date.Age

fun Age.getRangeForYears(): IntRange {
    return if (years < 7) {
         IntRange(0, 6)
    } else {
        val remainder = years % 7
        val start = years - remainder
        // use 6 since
        IntRange(start, start + 6)
    }
}
