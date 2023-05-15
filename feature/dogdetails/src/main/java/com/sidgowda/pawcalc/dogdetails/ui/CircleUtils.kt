package com.sidgowda.pawcalc.dogdetails.ui


fun convertedSweepAngle(sweepAngle: Float): Float {
    return when {
        sweepAngle in 0f..90f -> 90 - sweepAngle
        sweepAngle > 90f && sweepAngle <= 180f -> 360 - (sweepAngle - 90)
        sweepAngle > 180f && sweepAngle <= 270f -> 270 - (sweepAngle - 180)
        sweepAngle > 270f && sweepAngle <= 360f -> 180 - (sweepAngle - 270)
        else -> 0f
    }
}

fun convertedRadiusXY(radius: Float, sweepAngle: Float): Pair<Float, Float> {
    val x: Float
    val y: Float
    // cos and sin treats 0 as 3pm and goes counterclockwise
    // Our progress arc starts at top which is 12pm and goes clockwise so we will need to convert
    // our current angle to the counterclockwise angle cos and sin use.
    // Additionally, android coordinate system starts from (0,0) to (xMax, yMax)
    // so all negative coordinates will need to be converted to positive numbers.
    val convertedSweepAngle = convertedSweepAngle(sweepAngle).toDouble()
    if (convertedSweepAngle in 0f..90f) {
        x = radius * kotlin.math.cos(Math.toRadians(convertedSweepAngle)).toFloat() + radius
        y = radius - radius * kotlin.math.sin(Math.toRadians(convertedSweepAngle)).toFloat()
    } else if (convertedSweepAngle > 90f && convertedSweepAngle <= 180f) {
        x = radius + radius * kotlin.math.cos(Math.toRadians(convertedSweepAngle)).toFloat()
        y = radius + radius * - kotlin.math.sin(Math.toRadians(convertedSweepAngle)).toFloat()
    } else if (convertedSweepAngle > 180f && convertedSweepAngle <= 270f) {
        x = radius - radius * -kotlin.math.cos(Math.toRadians(convertedSweepAngle)).toFloat()
        y = radius + -radius * kotlin.math.sin(Math.toRadians(convertedSweepAngle)).toFloat()
    } else if (convertedSweepAngle > 270f && convertedSweepAngle <= 360f) {
        x = radius * kotlin.math.cos(Math.toRadians(convertedSweepAngle)).toFloat() + radius
        y = radius + -radius * kotlin.math.sin(Math.toRadians(convertedSweepAngle)).toFloat()
    }
    else {
        // edge case which should never be hit
        x = 0f
        y = 0f
    }
    return x to y
}
