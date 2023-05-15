package com.sidgowda.pawcalc.data.dogs.model

import com.sidgowda.pawcalc.data.dogs.model.formattedToString
import io.kotest.matchers.shouldBe
import org.junit.Test

class DogWeightStringFormatterTest {

    @Test
    fun `1dot0 should be emitted as 1`() {
        1.0.formattedToString() shouldBe "1"
    }

    @Test
    fun `85dot0 should be outputted as 85`() {
        85.0.formattedToString() shouldBe "85"
    }

    @Test
    fun `100dot356 should be outputted as 100dot36`() {
        100.356.formattedToString() shouldBe "100.36"
    }

    @Test
    fun `35dot2 should be outputted as 35dot2`() {
        35.2.formattedToString() shouldBe "35.2"
    }

    @Test
    fun `25dot1123456 should be outputted as 25dot11`() {
        25.1123456.formattedToString() shouldBe "25.11"
    }
}
