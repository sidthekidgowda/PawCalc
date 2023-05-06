package com.sidgowda.pawcalc.data.dogs

import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import io.kotest.matchers.doubles.shouldBeExactly
import org.junit.Test

class DogWeightCalculatorTest {

    @Test
    fun `1 lb should be dot45 kg`() {
        val weight = 1.0
        weight.toNewWeight(WeightFormat.KILOGRAMS) shouldBeExactly 0.45
    }

    @Test
    fun `1 kg should be 2dot20 lb`() {
        val weight = 1.0
        weight.toNewWeight(WeightFormat.POUNDS) shouldBeExactly 2.20
    }

    @Test
    fun `100 lb should be 45dot36 kg`() {
        val weight = 100.0
        weight.toNewWeight(WeightFormat.KILOGRAMS) shouldBeExactly 45.36
    }

    @Test
    fun `10 kgs should be 22dot05 lb`() {
        val weight = 10.0
        weight.toNewWeight(WeightFormat.POUNDS) shouldBeExactly 22.05
    }

    @Test
    fun `165 lbs should be 74dot84 kg`() {
        val weight = 165.0
        weight.toNewWeight(WeightFormat.KILOGRAMS) shouldBeExactly 74.84
    }

    @Test
    fun `75 lbs should be 34dot02 kg`() {
        val weight = 75.0
        weight.toNewWeight(WeightFormat.KILOGRAMS) shouldBeExactly 34.02
    }

    @Test
    fun `65 lbs should be 29dot48 kg`() {
        val weight = 65.0

        weight.toNewWeight(WeightFormat.KILOGRAMS) shouldBeExactly 29.48
    }
}
