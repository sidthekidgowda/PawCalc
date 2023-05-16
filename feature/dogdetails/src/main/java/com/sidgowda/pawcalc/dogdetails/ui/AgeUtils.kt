package com.sidgowda.pawcalc.dogdetails.ui

import com.sidgowda.pawcalc.data.date.Age

fun Age.getRangeForYears(): IntRange {
    val remainder = years % 7
    val start = years - remainder
    return IntRange(start, start + 6)
}
