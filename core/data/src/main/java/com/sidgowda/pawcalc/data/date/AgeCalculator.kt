package com.sidgowda.pawcalc.data.date

import com.sidgowda.pawcalc.date.dateFromLong
import com.sidgowda.pawcalc.date.localDateTimeInMilliseconds
import kotlin.math.round

internal enum class Month(val id: Int, val days: Int, val hasLeapYear: Boolean, val nextMonthId: Int) {
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

fun Age.toText(): String {
    val stringBuilder = StringBuilder()
    if (years > 0) {
        stringBuilder.append("${years}y")
        if (months > 0 || days > 0) {
            stringBuilder.append(" ")
        }
    }
    if (months > 0) {
        stringBuilder.append("${months}m")
        if (days > 0) {
            stringBuilder.append(" ")
        }
    }
    if (years == 0 && months == 0 || days > 0) {
        stringBuilder.append("${days}d")
    }
   return stringBuilder.toString()
}

/**
 * Algorithm to calculate number of years, months, and days from a birth date till today:
 * years = Today(years) - BirthDate(years), subtract 1 if birth month is past today's month
 * months = loop from start of given birth month till end month
 *          if the next month is not the end month, calculate number of days between birth dates of
 *          each month. Ex Jan 15 - Feb 15 is 31 days. Increment total month count
 *          if the next month is end month, and if the birth date happens before today,
 *              Increment total month count and calculate difference of Today from birth date.
 *              Ex If birth date is April 15 and today is April 18. Difference is 3 for total days
 *              and we increment total month count.
 *              otherwise, if birth date happens after today, calculate difference from
 *               current month total days - birth date days + days today. This difference is total days
 * days = Absolute difference remaining when there is leftover from birth date till today.
 */
fun String.toDogYears(
    today: String = dateFromLong(localDateTimeInMilliseconds())
): Age {
    val dateSplit = split("/")
    val monthsOfBirthDate = dateSplit.first().toInt()
    val daysOfBirthDate = dateSplit[1].toInt()
    val yearsOfBirthDate = dateSplit.last().toInt()

    val todaySplit = today.split("/")
    val monthsToday = todaySplit.first().toInt()
    val daysToday = todaySplit[1].toInt()
    val yearsToday = todaySplit.last().toInt()
    // make sure birth date is not after today
    check(
        yearsOfBirthDate < yearsToday ||
                monthsOfBirthDate <= monthsToday &&
                (
                    daysOfBirthDate >= daysToday && monthsOfBirthDate < monthsToday ||
                    daysOfBirthDate <= daysToday
                )
    )

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

/**
 * Algorithm to convert Birth Date of a dog to human years:
 * convert months and days to years, using 7 years to 12 months and 7 years to 365/366 days
 * years = round(dogYears * 7 + convertedDogMonths + convertedDogDaysToYears)
 * months = round(decimalPortionOfYears * 12)
 * days = round(decimalPortionOfMonths * 30) (Averaging 30 days per month)
 */
fun String.toHumanYears(
    today: String = dateFromLong(localDateTimeInMilliseconds())
): Age {
    val dogYears = toDogYears()
    val yearsToday = today.split("/").last().toInt()
    val humanYearsToMonthsRatio = 7.0 / 12.0
    val numberOfDaysInYear = if (yearsToday / 4 == 0) 366 else 365
    val humanYearsToDaysRatio = 7.0 / numberOfDaysInYear
    val monthsToYears = dogYears.months * humanYearsToMonthsRatio
    val daysToYears = dogYears.days * humanYearsToDaysRatio
    val numberOfYearsDecimal = dogYears.years * 7 + monthsToYears + daysToYears
    val numberOfYears = numberOfYearsDecimal.toInt()
    val numberOfMonthsDecimal = (numberOfYearsDecimal - numberOfYears) * 12.0
    val numberOfMonths = numberOfMonthsDecimal.toInt()
    val numberOfDaysDecimal = (numberOfMonthsDecimal - numberOfMonths) * 30.0
    val numberOfDays = round(numberOfDaysDecimal).toInt()

    return dogYears.copy(
        years = numberOfYears,
        months = numberOfMonths,
        days = numberOfDays
    )
}
