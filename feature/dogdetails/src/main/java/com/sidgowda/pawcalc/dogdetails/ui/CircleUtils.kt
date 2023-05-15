package com.sidgowda.pawcalc.dogdetails.ui


fun convertedSweepAngle(sweepAngle: Float): Float {
    return when {
        sweepAngle >= 0f && sweepAngle <= 90f -> 90 - sweepAngle
        sweepAngle > 90f && sweepAngle <= 180f -> 360 - (sweepAngle - 90)
        sweepAngle > 180f && sweepAngle <= 270f -> 270 - (sweepAngle - 180)
        sweepAngle > 270f && sweepAngle <= 360f -> 180 - (sweepAngle - 270)
        else -> 0f
    }
}

fun convertedRadiusXY(radius: Float, sweepAngle: Double): Pair<Float, Float> {
    val x: Float
    val y: Float
    if (sweepAngle >= 0f && sweepAngle <= 90f) {
        x = radius * kotlin.math.cos(Math.toRadians(sweepAngle)).toFloat() + radius
        y = radius - radius * kotlin.math.sin(Math.toRadians(sweepAngle)).toFloat()
    } else if (sweepAngle > 90f && sweepAngle <= 180f) {
        x = radius + radius * kotlin.math.cos(Math.toRadians(sweepAngle)).toFloat()
        y = radius + radius * - kotlin.math.sin(Math.toRadians(sweepAngle)).toFloat()
    } else if (sweepAngle > 180f && sweepAngle <= 270f) {
        x = radius - radius * -kotlin.math.cos(Math.toRadians(sweepAngle)).toFloat()
        y = radius + -radius * kotlin.math.sin(Math.toRadians(sweepAngle)).toFloat()
    } else if (sweepAngle > 270f && sweepAngle <= 360f) {
        x = radius * kotlin.math.cos(Math.toRadians(sweepAngle)).toFloat() + radius
        y = radius + -radius * kotlin.math.sin(Math.toRadians(sweepAngle)).toFloat()
    }
    else {
        x = 0f
        y = 0f
    }
    return x to y
}
