package com.sidgowda.pawcalc.data.date

import com.sidgowda.pawcalc.common.settings.DateFormat
import timber.log.Timber
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

internal const val DATE_FORMAT = "M/d/yyyy"
internal const val DATE_FORMAT_INTERNATIONAL = "d/M/yyyy"
internal const val TIME_ZONE = "UTC"

fun calendar(date: Long? = null): Calendar {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE))
    date?.let {
        calendar.timeInMillis = date
    }
    return calendar
}

internal fun localDateNow() = LocalDate.now(ZoneOffset.UTC)
internal fun localTimeNow() = LocalTime.now(ZoneOffset.UTC)

internal fun localDateTimeInMilliseconds() =
    LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()

fun dateFromLong(date: Long, isDateFormatInternational: Boolean = false): String {
    val localDate = try {
        Instant.ofEpochMilli(date).atZone(ZoneOffset.UTC).toLocalDate()
    } catch (e: Exception) {
        Timber.tag("DateUtils").e(e, "Failed to parse long: $date into a date")
        localDateNow()
    }
    return localDate.format(
        if (isDateFormatInternational) {
            DateTimeFormatter.ofPattern(DATE_FORMAT_INTERNATIONAL)
        } else {
            DateTimeFormatter.ofPattern(DATE_FORMAT)
        }
    )
}

fun dateToLong(date: String, isDateFormatInternational: Boolean = false): Long {
    return try {
        LocalDateTime.of(
            LocalDate.parse(
                date, if (isDateFormatInternational) {
                    DateTimeFormatter.ofPattern(DATE_FORMAT_INTERNATIONAL)
                } else {
                    DateTimeFormatter.ofPattern(DATE_FORMAT)
                }
            ),
            localTimeNow()
        )
            .atZone(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    } catch (e: Exception) {
        Timber.tag("DateUtils").e(e, "Failed to parse date: $date")
        LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
    }
}

fun String.dateToNewFormat(newDateFormat: DateFormat): String {
    val dateSplit = split("/")
    val stringBuilder = StringBuilder()
    val years = dateSplit.last()
    when (newDateFormat) {
        DateFormat.AMERICAN -> {
            // current date is international. convert to American
            val days = dateSplit.first()
            val months = dateSplit[1]
            stringBuilder.append(months).append("/").append(days).append("/")
        }
        DateFormat.INTERNATIONAL -> {
            // current date is American. convert to International
            val months = dateSplit.first()
            val days = dateSplit[1]
            stringBuilder.append(days).append("/").append(months).append("/")
        }
    }
    val newDate = stringBuilder.append(years).toString()
    Timber.tag("DateUtils").d("Old Date: $this, New Date: $newDate")
    return newDate
}
