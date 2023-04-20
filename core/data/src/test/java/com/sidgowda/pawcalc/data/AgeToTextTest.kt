package com.sidgowda.pawcalc.data

import com.sidgowda.pawcalc.data.date.Age
import com.sidgowda.pawcalc.data.date.toText
import io.kotest.matchers.shouldBe
import org.junit.Test

class AgeToTextTest {

    @Test
    fun `given age is 0d then 0d as text should be emitted`() {
        val age = Age(
            years = 0,
            months = 0,
            days = 0
        )
        age.toText() shouldBe "0d"
    }

    @Test
    fun `given age is 1d then 1d as text should be emitted`() {
        val age = Age(
            years = 0,
            months = 0,
            days = 1
        )

        age.toText() shouldBe "1d"
    }

    @Test
    fun `given age is 1m then 1m as text should be emitted`() {
        val age = Age(
            years = 0,
            months = 1,
            days = 0
        )

        age.toText() shouldBe "1m"
    }

    @Test
    fun `given age is 1y then 1y as text should be emitted`() {
        val age = Age(
            years = 1,
            months = 0,
            days = 0
        )

        age.toText() shouldBe "1y"
    }

    @Test
    fun `given age is 1y and 10d then 1y 1d as text should be emitted`() {
        val age = Age(
            years = 1,
            months = 0,
            days = 10
        )

        age.toText() shouldBe "1y 10d"
    }

    @Test
    fun `given age is 1y and 5m then 1y 5m as text should be emitted`() {
        val age = Age(
            years = 1,
            months = 5,
            days = 0
        )

        age.toText() shouldBe "1y 5m"
    }

    @Test
    fun `given age is 11m and 30d then 11m 30d as text should be emitted`() {
        val age = Age(
            years = 0,
            months = 11,
            days = 30
        )

        age.toText() shouldBe "11m 30d"
    }

    @Test
    fun `given age is 5y 3m 15d then 5y 3m 15d as text should be emitted`() {
        val age = Age(
            years = 5,
            months = 3,
            days = 15
        )

        age.toText() shouldBe "5y 3m 15d"
    }
}
