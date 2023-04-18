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

fun String.toDogYears(
    today: String = dateFromLong(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
): Age {
    val dateSplit = split("/")
    val monthsOfBirthDate = dateSplit.first().toInt()
    val daysOfBirthDate = dateSplit[1].toInt()
    val yearsOfBirthDate = dateSplit.last().toInt()

    val todaySplit = today.split("/")
    val monthsToday = todaySplit.first().toInt()
    val daysToday = todaySplit[1].toInt()
    val yearsToday = todaySplit.last().toInt()

    var totalMonthsCount = 0
    var totalDaysCount: Int
    val birthMonth = Month from monthsOfBirthDate
    var startMonth = Month from monthsOfBirthDate
    val endMonth = Month from monthsToday
    //edge case
    totalDaysCount = Math.abs(daysToday - daysOfBirthDate)

    while (startMonth != endMonth) {
        totalDaysCount = 0
        val nextMonth = Month from startMonth.nextMonthId
        val daysBetweenBirthDates = if (nextMonth != endMonth) {
            // Feb
            if (startMonth.hasLeapYear && yearsToday / 4 == 0 ) {
                // edge case
                startMonth.days + 1
            } else {
                startMonth.days
            }
        } else {
            if (daysOfBirthDate < daysToday) {
                // edge case
                totalMonthsCount++
                Math.abs(daysOfBirthDate - daysToday)
            } else  {
                startMonth.days - daysOfBirthDate + daysToday
            }
        }
        if (daysBetweenBirthDates >= startMonth.days) {
            totalMonthsCount++
        } else {
            totalDaysCount = daysBetweenBirthDates
        }
        startMonth = nextMonth
    }
    var yearsCount = yearsToday - yearsOfBirthDate
    if (birthMonth.id > endMonth.id) {
        yearsCount--
    }
    return Age(
        years = yearsCount,
        months = totalMonthsCount,
        days = totalDaysCount
    )
}

fun String.toHumanYears(
    today: String = dateFromLong(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
): Age {
    val dogYears = toDogYears()
    // 7 year quanitifer
    // if days and months are 0, 1 dog year = 7 human years
    // 7 years/365 days or 7/366 for leap year, days/years = 365/7
    //1 d = .0191y,
    //1 y = 52.14 d
    //1 month dog month = 31 days
    return dogYears.copy(
        years = dogYears.years * 7
    )
}
