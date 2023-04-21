package com.sidgowda.pawcalc.data.date

import com.sidgowda.pawcalc.data.date.Age
import com.sidgowda.pawcalc.data.date.toHumanYears
import io.kotest.matchers.shouldBe
import org.junit.Test

class HumanYearsCalculatorTest {

    @Test
    fun `age should be zero if birth date is same as today at 4-20-2023`() {
        val age = "4/20/2023".toHumanYears(today = "4/20/2023")

        age shouldBe Age(
            years = 0,
            months = 0,
            days = 0
        )
    }

    @Test
    fun `age should be 7 days if birth date is one day before today at 4-20-2023`() {
        val age = "4/19/2023".toHumanYears(today = "4/20/2023")

        age shouldBe Age(
            years = 0,
            months = 0,
            days = 7
        )
    }

    @Test
    fun `age should be 7 months if birth date is one month before today at 4-20-2023`() {
        val age = "3/20/2023".toHumanYears(today = "4/20/2023")

        age shouldBe Age(
            years = 0,
            months = 7,
            days = 0
        )
    }

    @Test
    fun `age should be 7 year if birth date is one year before today at 4-20-2023`() {
        val age = "4/20/2022".toHumanYears(today = "4/20/2023")

        age shouldBe Age(
            years = 7,
            months = 0,
            days = 0
        )
    }

    @Test
    fun `age should be 26 years if birth date is 7-30-2019 and today is 4-20-2023`() {
        val age = "7/30/2019".toHumanYears(today = "4/20/2023")

        age shouldBe Age(
            years = 26,
            months = 0,
            days = 25
        )
    }

    @Test
    fun `age should be 30 years 3months 30 days if birth date is 12-20-2018 and today is 4-20-2023`() {
        val age = "12/20/2018".toHumanYears(today = "4/20/2023")

        age shouldBe Age(
            years = 30,
            months = 3,
            days = 30
        )
    }

    @Test
    fun `age should be 3years 3months 30 days if birth date is on leap year 4-29-2020 and today is 2-29-2024`() {
        val age = "4/29/2020".toHumanYears(today = "2/29/2024")

        age shouldBe Age(
            years = 26,
            months = 10,
            days = 0
        )
    }

    @Test
    fun `age should be 3 months 7 days if birth date is on leap year 2-15-2024 and today is 2-29-2024`() {
        val age = "2/15/2024".toHumanYears(today = "2/29/2024")

        age shouldBe Age(
            years = 0,
            months = 3,
            days = 7
        )
    }

    @Test
    fun `age should be 28 years 0months 0 days if birth date is on leap year 2-29-2020 and today is 2-29-2024`() {
        val age = "2/29/2020".toHumanYears(today = "2/29/2024")

        age shouldBe Age(
            years = 28,
            months = 0,
            days = 0
        )
    }
}
