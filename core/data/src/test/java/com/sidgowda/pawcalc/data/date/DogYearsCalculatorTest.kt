package com.sidgowda.pawcalc.data.date

import io.kotest.matchers.shouldBe
import org.junit.Test

class DogYearsCalculatorTest {

    @Test(expected = IllegalStateException::class)
    fun `should throw IllegalStateException if birth date 12-20-2024 is after today 4-15-2023`() {
        "12/20/2024".toDogYears(today = "4/15/2023")
    }


    @Test(expected = IllegalStateException::class)
    fun `should throw IllegalStateException if birth date 11-10-2023 is after today 4-15-2023`() {
        "11/17/2023".toDogYears(today = "4/15/2023")
    }


    @Test(expected = IllegalStateException::class)
    fun `should throw IllegalStateException if birth date 4-17-2023 is after today 4-15-2023`() {
        val age = "4/17/2023".toDogYears(today = "4/15/2023")
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw IllegalStateException if birth date is 4-18-2023 and today is 3-20-2023`() {
        val age = "4/18/2023".toDogYears(today = "3/20/2023")
    }

    @Test
    fun `age should be zero if birth date is same as today at 4-19-2023`() {
        val age = "4/19/2023".toDogYears(today = "4/19/2023")

        age shouldBe Age(
            years = 0,
            months = 0,
            days = 0
        )
    }

    @Test
    fun `age should be 1 day if birth date is one day less than today at 4-19-2023`() {
        val age = "4/18/2023".toDogYears(today = "4/19/2023")

        age shouldBe Age(
            years = 0,
            months = 0,
            days = 1
        )
    }

    @Test
    fun `age should be 20 days if born on 3-31-2023 and today is 4-20-23`() {
        val age = "3/31/2023".toDogYears(today = "4/20/2023")

        age shouldBe Age(
            years = 0,
            months = 0,
            days = 20
        )
    }

    @Test
    fun `age should be 1 month if birth date is 3-20-2023 and today is 4-20-2023`() {
        val age = "3/20/2023".toDogYears(today = "4/20/2023")

        age shouldBe Age(
            years = 0,
            months = 1,
            days = 0
        )
    }

    @Test
    fun `age should be 1 month and 3 days if birth date is in 2-17-2021 and today is 3-20-2021`() {
        val age = "2/17/2021".toDogYears(today = "3/20/2021")

        age shouldBe Age(
            years = 0,
            months = 1,
            days = 3
        )
    }

    @Test
    fun `age should be 1 month and 3 days if birth date of 2-17-2024 is on a leap year and today is 3-20-2024`() {
        val age = "2/17/2024".toDogYears(today = "3/20/2024")

        age shouldBe Age(
            years = 0,
            months = 1,
            days = 3
        )
    }

    @Test
    fun `age should be 30 days if born on 5-31-2023 and today is 6-30-2023`() {
        val age = "5/31/2023".toDogYears(today = "6/30/2023")

        age shouldBe Age(
            years = 0,
            months = 0,
            days = 30
        )
    }

    @Test
    fun `age should be 1 year if born on 1-1-2022 and today is 1-1-2023`() {
        val age = "1/1/2022".toDogYears(today = "1/1/2023")

        age shouldBe Age(
            years = 1,
            months = 0,
            days = 0
        )
    }

    @Test
    fun `age should be 32 years 3 months and 30 days if born on 12-20-1990 and today is 4-19-2023`() {
        val age = "12/20/1990".toDogYears(today = "4/19/2023")

        age shouldBe Age(
            32,
            3,
            30
        )
    }

    @Test
    fun `age should be 3 years 1 month and 20 days if born on 2-29-2020 and today is 4-17-2023`() {
        val age = "2/29/2020".toDogYears(today = "4/17/2023")

        age shouldBe Age(
            3,
            1,
            19
        )
    }

    @Test
    fun `age should be 3 years 8 months and 20 days if born on 7-30-2019 and today is 4-19-2023`() {
        val age = "7/30/2019".toDogYears(today = "4/19/2023")

        age shouldBe Age(
            3,
            8,
            20
        )
    }

    @Test
    fun `age should be 30 years 0 months and 21 days if born on 3-30-1993 and today is 4-20-2023`() {
        val age = "3/30/1993".toDogYears(today = "4/20/2023")

        age shouldBe Age(
            30,
            0,
            21
        )
    }

    @Test
    fun `age should be 19 years 2 months and 14 days if born on 2-6-2024 and today is 4-20-2004`() {
        val age = "2/6/2004".toDogYears(today = "4/20/2023")

        age shouldBe Age(
            19,
            2,
            14
        )
    }

}
