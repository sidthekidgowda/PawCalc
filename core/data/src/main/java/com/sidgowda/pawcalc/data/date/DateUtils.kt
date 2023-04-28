package com.sidgowda.pawcalc.date

import android.util.Log
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

internal const val DATE_FORMAT = "M/d/yyyy"
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

fun dateFromLong(date: Long): String {
    val localDate = try {
        Instant.ofEpochMilli(date).atZone(ZoneOffset.UTC).toLocalDate()
    } catch (e: Exception) {
        Log.e("DateUtils", "Failed to parse long: $date into a date", e)
        localDateNow()
    }
    return localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
}

fun dateToLong(date: String): Long {
    return try {
        LocalDateTime.of(
            LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT)),
            localTimeNow()
        )
        .atZone(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
    } catch (e: Exception) {
        Log.e("DateUtils", "Failed to parse date: $date", e)
        LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
    }
}
