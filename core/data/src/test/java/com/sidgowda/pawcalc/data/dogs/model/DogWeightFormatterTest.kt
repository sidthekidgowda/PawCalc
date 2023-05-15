package com.sidgowda.pawcalc.data.dogs.model

import io.kotest.matchers.shouldBe
import org.junit.Test

class DogWeightFormatterTest {

    @Test
    fun `1dot0000 should be emitted as 1dot0`() {
        1.0000.formattedToTwoDecimals() shouldBe 1.0
    }

    @Test
    fun `85dot00000000 should be outputted as 85dot0`() {
        85.00000000.formattedToTwoDecimals() shouldBe 85.0
    }

    @Test
    fun `100dot356 should be outputted as 100dot36`() {
        100.356.formattedToTwoDecimals() shouldBe 100.36
    }

    @Test
    fun `35dot2 should be outputted as 35dot2`() {
        35.2.formattedToTwoDecimals() shouldBe 35.2
    }

    @Test
    fun `25dot1123456 should be outputted as 25dot11`() {
        25.1123456.formattedToTwoDecimals() shouldBe 25.11
    }
}
