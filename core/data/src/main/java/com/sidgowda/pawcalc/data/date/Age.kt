package com.sidgowda.pawcalc.data.date

data class Age(
    val years: Int,
    val months: Int,
    val days: Int
) {
    companion object {
        const val DATE_FORMAT_AMERICAN = "M/d/yyyy"
        const val DATE_FORMAT_INTERNATIONAL = "d/M/yyyy"
        const val TIME_ZONE = "UTC"
    }
}
