package com.sidgowda.pawcalc.dogdetails.ui

import com.sidgowda.pawcalc.data.date.Age
import timber.log.Timber

fun Age.getRangeForYears(): IntRange {
    val remainder = years % 7
    val start = years - remainder
    val range = IntRange(start, start + 6)
    Timber.tag("AgeUtils")
        .d( "Years start: ${range.start}, Years end: ${range.endInclusive}")
    return range
}
