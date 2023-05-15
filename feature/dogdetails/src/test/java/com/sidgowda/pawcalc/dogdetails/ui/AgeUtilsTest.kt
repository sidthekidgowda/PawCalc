package com.sidgowda.pawcalc.dogdetails.ui

import com.sidgowda.pawcalc.data.date.Age
import io.kotest.matchers.shouldBe
import org.junit.Test

class AgeUtilsTest {

    @Test
    fun `when years is 0, then range starts from 0 and ends at 6`() {
        Age(
            years = 0,
            months = 0,
            days = 0
        ).getRangeForYears() shouldBe IntRange(0, 6)
    }

    @Test
    fun `when years is 4 then range starts from 0 and ends at 6`() {
        Age(
            years = 4,
            months = 0,
            days = 0
        ).getRangeForYears() shouldBe IntRange(0, 6)
    }

    @Test
    fun `when years is 7 then range starts from 7 and ends at 13`() {
        Age(
            years = 7,
            months = 0,
            days = 0
        ).getRangeForYears() shouldBe IntRange(7, 13)
    }

    @Test
    fun `when years is 15 then range starts from 14 and ends at 20`() {
        Age(
            years = 15,
            months = 0,
            days = 0
        ).getRangeForYears() shouldBe IntRange(14, 20)
    }

    @Test
    fun `when years is 30 then range starts from 28 and ends at 34`() {
        Age(
            years = 30,
            months = 0,
            days = 0
        ).getRangeForYears() shouldBe IntRange(28, 34)
    }
}
