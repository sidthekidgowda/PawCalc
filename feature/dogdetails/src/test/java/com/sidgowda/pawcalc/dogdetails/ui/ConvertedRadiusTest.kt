package com.sidgowda.pawcalc.dogdetails.ui

import io.kotest.matchers.shouldBe
import org.junit.Test

class ConvertedRadiusTest {

    @Test
    fun `when radius is 100 and sweep angle is 0 then x is 100 and y is 0`() {
        radiusXY(100f, 0f) shouldBe Pair(100f, 0f)
    }

    @Test
    fun `when radius is 100 and sweep angle is 90 then x is 200 and y is 100`() {
        radiusXY(100f, 90f) shouldBe Pair(200f, 100f)
    }

    @Test
    fun `when radius is 100 and sweep angle is 180 then x is 100 and y is 200`() {
        radiusXY(100f, 180f) shouldBe Pair(100f, 200f)
    }

    @Test
    fun `when radius is 100 and sweep angle is 270 then x is 0 and y is 100`() {
        radiusXY(100f, 270f) shouldBe Pair(0f, 100f)
    }

    @Test
    fun `when radius is 100 and sweep angle is 360 then x is 100 and y is 0`() {
        radiusXY(100f, 360f) shouldBe Pair(100f, 0f)
    }
}
