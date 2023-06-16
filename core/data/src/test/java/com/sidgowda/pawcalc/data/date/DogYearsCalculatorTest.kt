package com.sidgowda.pawcalc.data.date

import com.sidgowda.pawcalc.common.settings.DateFormat
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

    @Test(expected = IllegalStateException::class)
    fun `when Date Format is International, should throw IllegalStateException if birth date is 18-4-2023 and today is 3-20-2023`() {
        val age = "18/4/2023".toDogYears(today = "3/20/2023", DateFormat.INTERNATIONAL)
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
    fun `when DateFormat is International age should be 1 day if birth date is one day less than today at 4-19-2023`() {
        val age = "18/4/2023".toDogYears(today = "4/19/2023", DateFormat.INTERNATIONAL)

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
    fun `when DateFormat is International, age should be 1 month if birth date is 3-20-2023 and today is 4-20-2023`() {
        val age = "20/3/2023".toDogYears(today = "4/20/2023", DateFormat.INTERNATIONAL)

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
    fun `age should be 5 months and 23 days if birth date is in 2-28-2020 and today is 8-20-2020`() {
        val age = "2/28/2020".toDogYears(today = "8/20/2020")

        age shouldBe Age(
            years = 0,
            months = 5,
            days = 23
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
    fun `when DateFormat is International, age should be 30 days if born on 31-5-2023 and today is 6-30-2023`() {
        val age = "31/5/2023".toDogYears(today = "6/30/2023", DateFormat.INTERNATIONAL)

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
            years = 32,
            months = 3,
            days = 30
        )
    }

    @Test
    fun `age should be 3 years 1 month and 20 days if born on 2-29-2020 and today is 4-17-2023`() {
        val age = "2/29/2020".toDogYears(today = "4/17/2023")

        age shouldBe Age(
            years = 3,
            months = 1,
            days = 19
        )
    }

    @Test
    fun `age should be 3 years 1 month and 20 days if born on 2-29-2020 and today is 3-29-2023`() {
        val age = "2/29/2020".toDogYears(today = "3/29/2023")

        age shouldBe Age(
            years = 3,
            months = 1,
            days = 0
        )
    }

    @Test
    fun `age should be 3 years 3 month and 30 days if born on 2-29-2020 and today is 6-28-2023`() {
        val age = "2/29/2020".toDogYears(today = "6/28/2023")

        age shouldBe Age(
            years = 3,
            months = 3,
            days = 30
        )
    }

    @Test
    fun `age should be 3 years 8 months and 20 days if born on 7-30-2019 and today is 4-19-2023`() {
        val age = "7/30/2019".toDogYears(today = "4/19/2023")

        age shouldBe Age(
            years = 3,
            months = 8,
            days = 20
        )
    }

    @Test
    fun `age should be 30 years 0 months and 21 days if born on 3-30-1993 and today is 4-20-2023`() {
        val age = "3/30/1993".toDogYears(today = "4/20/2023")

        age shouldBe Age(
            years = 30,
            months = 0,
            days = 21
        )
    }

    @Test
    fun `age should be 3 years 1 months and 2 days if born on 2-29-2020 and today is 3-31-2023`() {
        val age = "2/29/2020".toDogYears(today = "3/31/2023")

        age shouldBe Age(
            years = 3,
            months = 1,
            days = 2
        )
    }

    @Test
    fun `age should be 0 years 0 months and 16 days if born on 2-29-2020 and today is 3-16-2020`() {
        val age = "2/29/2020".toDogYears(today = "3/16/2020")

        age shouldBe Age(
            years = 0,
            months = 0,
            days = 16
        )
    }

    @Test
    fun `age should be 19 years 2 months and 14 days if born on 2-6-2004 and today is 4-20-2023`() {
        val age = "2/6/2004".toDogYears(today = "4/20/2023")

        age shouldBe Age(
            years = 19,
            months = 2,
            days = 14
        )
    }

    @Test
    fun `when DateFormat is in International while born on 6-2-2004 and today is 4-20-2023 then age should be 19 years 2 months and 14 days`() {
        val age = "6/2/2004".toDogYears(today = "4/20/2023", DateFormat.INTERNATIONAL)

        age shouldBe Age(
            years = 19,
            months = 2,
            days = 14
        )
    }

    @Test
    fun `given DateFormat is International and born on 30-2-1993, age should be 30 years 0 months and 21 days when today is 4-20-2023`() {
        val age = "30/3/1993".toDogYears(today = "4/20/2023", DateFormat.INTERNATIONAL)

        age shouldBe Age(
            years = 30,
            months = 0,
            days = 21
        )
    }

    @Test
    fun `given born on 6-21-2019 and today is 6-15-2023, age should be 3 years 11 months 25 days`() {
        val age = "6/21/2019".toDogYears(today = "6/15/2023")

        age shouldBe Age(
            years = 3,
            months = 11,
            days = 25
        )
    }

    @Test
    fun `given born on 6-15-2019 and today is 6-21-2023, age should be 4 years 0 months 6 days`() {
        val age = "6/15/2019".toDogYears(today = "6/21/2023")

        age shouldBe Age(
            years = 4,
            months = 0,
            days = 6
        )
    }

    @Test
    fun `given born on 6-21-2019 and today is 6-21-2023, age should be 4 years 0 months 0 days`() {
        val age = "6/21/2019".toDogYears(today = "6/21/2023")

        age shouldBe Age(
            years = 4,
            months = 0,
            days = 0
        )
    }

    @Test
    fun `age should be 4 years 4months 0 days if birth date is 12-20-2018 and today is 4-20-2023`() {
        val age = "12/20/2018".toDogYears(today = "4/20/2023")

        age shouldBe Age(
            years = 4,
            months = 4,
            days = 0
        )
    }

    @Test
    fun `age should be 3 years 10 months if birth date is on leap year 4-29-2020 and today is 2-29-2024`() {
        val age = "4/29/2020".toDogYears(today = "2/29/2024")

        age shouldBe Age(
            years = 3,
            months = 10,
            days = 0
        )
    }

    @Test
    fun `age should be 1 year if birth date is one year before today at 4-20-2023`() {
        val age = "4/20/2022".toDogYears(today = "4/20/2023")

        age shouldBe Age(
            years = 1,
            months = 0,
            days = 0
        )
    }

}
