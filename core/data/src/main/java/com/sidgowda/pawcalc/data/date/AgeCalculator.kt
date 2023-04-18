package com.sidgowda.pawcalc.data.date

import com.sidgowda.pawcalc.date.dateFromLong
import java.time.LocalDateTime
import java.time.ZoneOffset

enum class Month(val id: Int, val days: Int, val hasLeapYear: Boolean, val nextMonthId: Int) {
    JAN(1, 31, false, 2),
    FEB(2,28, hasLeapYear = true, 3),
    MARCH(3,31, hasLeapYear = false, 4),
    APRIL(4,30, hasLeapYear = false, 5),
    MAY(5,31, hasLeapYear = false, 6),
    JUNE(6,30, hasLeapYear = false, 7),
    JULY(7,31, hasLeapYear = false, 8),
    AUGUST(8,31, hasLeapYear = false, 9),
    SEPTEMBER(9,30, hasLeapYear = false, 10),
    OCTOBER(10,31, hasLeapYear = false, 11),
    NOVEMBER(11,30, hasLeapYear = false, 12),
    DECEMBER(12,31, hasLeapYear = false, 1);

    companion object {
        infix fun from(monthId: Int): Month = Month.values().first { it.id == monthId }
    }
}

fun String.toDogYears(): Age {
    val dateSplit = split("/")
    val monthsBirthDate = dateSplit.first().toInt()
    val daysBirthDate = dateSplit[1].toInt()
    val yearsBirthDate = dateSplit.last().toInt()

    val today = dateFromLong(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
    val todaySplit = today.split("/")
    val monthsToday = todaySplit.first().toInt()
    val daysToday = todaySplit[1].toInt()
    val yearsToday = todaySplit.last().toInt()

    var totalMonthsCount = 0
    var totalDaysCount = 0
    var startMonth = Month from monthsBirthDate
    val endMonth = Month from monthsToday

    // consider edge cases, May 31 to June 30
    // consider leap year
    while (startMonth != endMonth) {
        val nextMonth = Month from startMonth.nextMonthId
        val daysCount = if (nextMonth != endMonth) {
            // Feb
            if (startMonth.hasLeapYear && yearsToday / 4 == 0 ) {
                startMonth.days + 1
            } else if (startMonth.days > nextMonth.days){
                startMonth.days - 1
            } else {
                startMonth.days
            }
        } else {
            startMonth.days - daysBirthDate + daysToday
        }
        // dec 20 to jan 5 = dec 20 to dec 31 = 11 days, jan 1 - jan 5 = 5, total = 16 days
        // Dec 20 to Jan 5, less than 31 days = 0 months + 11 days + 5days
        // Dec 20 to Jan 20, 31 days = 1 month
        if (daysCount == startMonth.days) {
            totalMonthsCount++
        } else {
            totalDaysCount = daysCount
        }
        startMonth = nextMonth
    }
    //Dec 20, 1990 - April 17, 2023 = 33
    var yearsCount = yearsToday - yearsBirthDate
    if (startMonth.id > endMonth.id) {
        yearsCount--
    }
    return Age(
        years = yearsCount,
        months = totalMonthsCount,
        days = totalDaysCount
    )
}

fun String.toHumanYears(): Age {
    val dogYears = toDogYears()
    return dogYears.copy(
        years = dogYears.years * 7
    )
}
