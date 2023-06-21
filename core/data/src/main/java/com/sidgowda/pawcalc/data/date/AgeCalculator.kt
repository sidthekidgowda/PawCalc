package com.sidgowda.pawcalc.data.date

import android.content.Context
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.data.R
import timber.log.Timber
import kotlin.math.round

internal enum class Month(val id: Int, val days: Int, val prevMonthId: Int) {
    JAN(1, 31, 12),
    FEB(2,28, 1),
    MARCH(3,31, 2),
    APRIL(4,30, 3),
    MAY(5,31, 4),
    JUNE(6,30, 5),
    JULY(7,31, 6),
    AUGUST(8,31, 7),
    SEPTEMBER(9,30, 8),
    OCTOBER(10,31,  9),
    NOVEMBER(11,30,  10),
    DECEMBER(12,31, 11);

    companion object {
        infix fun from(monthId: Int): Month = Month.values().first { it.id == monthId }
    }
}

fun daysInMonthToday(
    today: String = dateFromLong(localDateTimeInMilliseconds()),
    days: Int
): Int {
    Timber.tag("AgeCalculator").d("Today is $today")
    val todaySplit = today.split("/")
    val monthsToday = todaySplit.first().toInt()
    val month = Month from monthsToday
    return if (month.days < days) {
        days + 1
    } else {
        month.days - 1
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
    Timber.tag("AgeCalculator").d("Age.toText() is ${stringBuilder}")
    return stringBuilder.toString()
}
fun Age.toAccessibilityText(context: Context): String {
    val stringBuilder = StringBuilder()
    if (years > 0) {
        stringBuilder.append(context.getString(R.string.years, years))
        if (months > 0 || days > 0) {
            stringBuilder.append(" ")
        }
    }
    if (months > 0) {
        stringBuilder.append(context.getString(R.string.months, months))
        if (days > 0) {
            stringBuilder.append(" ")
        }
    }
    if (years == 0 && months == 0 || days > 0) {
        stringBuilder.append(context.getString(R.string.days, days))
    }
    Timber.tag("AgeCalculator").d("Age.toAccessibilityText() is ${stringBuilder}")
    return stringBuilder.toString()
}

/**
 * Algorithm to calculate number of years, months, and days from a birth date till today:
 * years = YearsToday - YearsBirthDate, subtract by 1 if birthDate is after today and
 *          months of birth date is more or equal to monthsToday
 *          and days and months are not equal.
 *          ex: birthdate 12/20/2019, today 4/20/2021, 2021 - 2019 is 2 years
 *          but real age is 1 year, X months, Y days
 *
 * months = if (monthsOfBirthDate > monthsToday),
 *          find difference between Dec and birthMonth, and add monthsToday
 *          subtract by 1 if daysOfBirthDate is more than daysToday
 *       else if (monthsOfBirthDate <= monthsToday)
 *          if (monthsOfBirthDate == monthsToday) 11
 *          else monthsToday - monthsBirthDate - 1
*        else
 *          monthsToday - monthsBirthDate
 *
 * days = if (daysBirthDate <= daysToday)
 *             daysToday - daysBirthDate
*         else
 *         find difference from birthDay to previousMonth number of days and then add daysOfToday
 *         if leap year, add 1
 */
fun String.toDogYears(
    today: String = dateFromLong(localDateTimeInMilliseconds()),
    dateFormat: DateFormat = DateFormat.AMERICAN
): Age {
    val dateSplit = split("/")
    val monthsOfBirthDate: Int
    val daysOfBirthDate: Int
    if (dateFormat == DateFormat.AMERICAN) {
        monthsOfBirthDate = dateSplit.first().toInt()
        daysOfBirthDate = dateSplit[1].toInt()
    } else {
        daysOfBirthDate = dateSplit.first().toInt()
        monthsOfBirthDate = dateSplit[1].toInt()
    }
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

    val totalMonthsCount = if (monthsOfBirthDate > monthsToday) {
        // birthdate = 11/5/2015, today = 8/15/2022
        var total = 12 - monthsOfBirthDate + monthsToday
        if (daysOfBirthDate > daysToday) {
            total--
        }
        total
    } else if (daysOfBirthDate > daysToday) {
        if (monthsOfBirthDate == monthsToday) {
            // birthdate = 12/10/2020, today = 12/1/2021,
            11
        } else {
            // birthdate = 8/20/2021, today = 9/1/2022
            monthsToday - monthsOfBirthDate - 1
        }
    } else {
        // birthdate = 5/10/2022, today = 8/20/2023
        monthsToday - monthsOfBirthDate
    }

    val numOfYears = yearsToday - yearsOfBirthDate
    val totalYearsCount: Int = if (daysToday <= daysOfBirthDate &&
        (monthsOfBirthDate > monthsToday || monthsOfBirthDate == monthsToday)
        && !(daysToday == daysOfBirthDate && monthsOfBirthDate == monthsToday)
    ) {
        // subtract by 1 if birthDate is after today and
        // months of birth date is more or equal to monthsToday
        // and days and months are not equal
        // ex: birthdate 12/20/2019, today 4/20/2021, 2021 - 2019 is 2 years
        // but real age is 1 year and x months y days
        numOfYears - 1
    } else {
        numOfYears
    }

    val totalDaysCount = if (daysOfBirthDate <= daysToday) {
        // birthdate = 3/20/2021, today = 4/22/2021
        daysToday - daysOfBirthDate
    } else {
        // daysOfBirthDate > daysToday
        // birthDate = 11/25/2021, today = 12/5/2022
        val currentMonth = Month from monthsToday
        val previousMonthId = currentMonth.prevMonthId
        val previousMonth = Month from previousMonthId
        // leap year
        var diffToEndOfMonth = previousMonth.days - daysOfBirthDate
        if (previousMonth == Month.FEB && yearsToday % 4 == 0 && yearsToday % 100 != 0) {
            diffToEndOfMonth++
        }
        diffToEndOfMonth + daysToday
    }

    Timber.tag("AgeCalculator")
        .d("Dog Years: $totalYearsCount years $totalMonthsCount months $totalDaysCount days")
    return Age(
        years = totalYearsCount,
        months = totalMonthsCount,
        days = totalDaysCount
    )
}

/**
 * Algorithm to convert Birth Date of a dog to human years:
 * convert months and days to years, using 7 years to 12 months and 7 years to 365/366 days
 * years = toInt(dogYears * 7 + convertedDogMonths + convertedDogDaysToYears)
 * months = toInt(decimalPortionOfYears * 12)
 * days = round(decimalPortionOfMonths * 30) (Averaging 30 days per month)
 */
fun String.toHumanYears(
    today: String = dateFromLong(localDateTimeInMilliseconds()),
    dateFormat: DateFormat = DateFormat.AMERICAN
): Age {
    val dateSplit = split("/")
    val monthsOfBirthDate: Int
    val daysOfBirthDate: Int
    if (dateFormat == DateFormat.AMERICAN) {
        monthsOfBirthDate = dateSplit.first().toInt()
        daysOfBirthDate = dateSplit[1].toInt()
    } else {
        daysOfBirthDate = dateSplit.first().toInt()
        monthsOfBirthDate = dateSplit[1].toInt()
    }
    val dogYears = toDogYears(today, dateFormat)
    val yearsToday = today.split("/").last().toInt()
    val humanYearsToMonthsRatio = 7.0 / 12.0
    val numberOfDaysInYear =
        if (yearsToday % 4 == 0 && yearsToday % 100 != 0 &&
            (monthsOfBirthDate > 2 || (monthsOfBirthDate == 2 && daysOfBirthDate == 29))
        ) {
            366
        } else {
            365
        }
    val humanYearsToDaysRatio = 7.0 / numberOfDaysInYear
    val monthsToYears = dogYears.months * humanYearsToMonthsRatio
    val daysToYears = dogYears.days * humanYearsToDaysRatio
    val numberOfYearsDecimal = dogYears.years * 7.0 + monthsToYears + daysToYears
    val numberOfYears = numberOfYearsDecimal.toInt()
    val numberOfMonthsDecimal = (numberOfYearsDecimal - numberOfYears) * 12.0
    val numberOfMonths = numberOfMonthsDecimal.toInt()
    val numberOfDaysDecimal = (numberOfMonthsDecimal - numberOfMonths) * 30.0
    val numberOfDays = round(numberOfDaysDecimal).toInt()

    Timber.tag("AgeCalculator")
        .d("Human Years: $numberOfYears years $numberOfMonths months $numberOfDays days")
    return Age(
        years = numberOfYears,
        months = numberOfMonths,
        days = numberOfDays
    )
}
