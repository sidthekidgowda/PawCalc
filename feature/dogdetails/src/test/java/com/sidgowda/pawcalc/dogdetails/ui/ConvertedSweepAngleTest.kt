package com.sidgowda.pawcalc.dogdetails.ui

import io.kotest.matchers.shouldBe
import org.junit.Test

class ConvertedSweepAngleTest {

    @Test
    fun `when start angle is 0, then converted sweep angle is 90`() {
        convertedSweepAngle(0f) shouldBe 90f
    }

    @Test
    fun `when start angle is 30, then converted sweep angle is 60`() {
        convertedSweepAngle(30f) shouldBe 60f
    }

    @Test
    fun `when start angle is 90, then converted sweep angle is 0`() {
        convertedSweepAngle(90f) shouldBe 0f
    }

    @Test
    fun `when start angle is 120, then converted sweep angle is 330`() {
        convertedSweepAngle(120f) shouldBe 330f
    }


    @Test
    fun `when start angle is 180, then converted sweep angle is 270`() {
        convertedSweepAngle(180f) shouldBe 270f
    }

    @Test
    fun `when start angle is 210, then converted sweep angle is 240`() {
        convertedSweepAngle(210f) shouldBe 240f
    }

    @Test
    fun `when start angle is 270, then converted sweep angle is 180`() {
        convertedSweepAngle(270f) shouldBe 180f
    }

    @Test
    fun `when start angle is 290, then converted sweep angle is 160`() {
        convertedSweepAngle(290f) shouldBe 160f
    }

    @Test
    fun `when start angle is 360, then converted sweep angle is 90`() {
        convertedSweepAngle(360f) shouldBe 90f
    }
}
