package com.sidgowda.pawcalc.common.settings

import io.kotest.matchers.shouldBe
import org.junit.Test

class WeightFormatTest {

    @Test
    fun `index 0 should return WeightFormat Pounds`() {
        WeightFormat.from(0) shouldBe WeightFormat.POUNDS
    }

    @Test
    fun `index 1 should return WeightFormat Kilograms`() {
        WeightFormat.from(1) shouldBe WeightFormat.KILOGRAMS
    }

    @Test(expected = NoSuchElementException::class)
    fun `index 2 should throw NoSuchElementException`() {
        WeightFormat.from(2)
    }
}
