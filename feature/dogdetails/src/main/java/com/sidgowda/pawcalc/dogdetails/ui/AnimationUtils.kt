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
fun convertedRadiusX(radius: Float, sweepAngle: Double): Float {
    return if (sweepAngle >= 0f && sweepAngle <= 90f) {
        radius * kotlin.math.cos(Math.toRadians(sweepAngle)).toFloat() + radius
    } else if (sweepAngle > 90f && sweepAngle <= 180f) {
        radius + radius * kotlin.math.cos(Math.toRadians(sweepAngle)).toFloat()
    } else if (sweepAngle > 180f && sweepAngle <= 270f) {
        radius - radius * -kotlin.math.cos(Math.toRadians(sweepAngle)).toFloat()
    } else if (sweepAngle > 270f && sweepAngle <= 360f) {
        radius * kotlin.math.cos(Math.toRadians(sweepAngle)).toFloat() + radius
    }
    else {
        0f
    }
}

fun convertedRadiusY(radius: Float, sweepAngle: Double): Float {
    return if (sweepAngle >= 0f && sweepAngle <= 90f) {
        radius - radius * kotlin.math.sin(Math.toRadians(sweepAngle)).toFloat()
    } else if (sweepAngle > 90f && sweepAngle <= 180f) {
        radius + radius * - kotlin.math.sin(Math.toRadians(sweepAngle)).toFloat()
    } else if (sweepAngle > 180f && sweepAngle <= 270f) {
        radius + -radius * kotlin.math.sin(Math.toRadians(sweepAngle)).toFloat()
    } else if (sweepAngle > 270f && sweepAngle <= 360f) {
        radius + -radius * kotlin.math.sin(Math.toRadians(sweepAngle)).toFloat()
    } else {
        0f
    }
}
