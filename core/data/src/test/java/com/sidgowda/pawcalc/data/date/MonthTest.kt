package com.sidgowda.pawcalc.data.date

import com.sidgowda.pawcalc.data.date.Month
import io.kotest.matchers.shouldBe
import org.junit.Test

class MonthTest {

    @Test
    fun `given id 1 then month is Jan`() {
        Month.from(1) shouldBe Month.JAN
    }

    @Test
    fun `given id 2 then month is Feb`() {
        Month.from(2) shouldBe Month.FEB
    }

    @Test
    fun `given id 3 then month is March`() {
        Month.from(3) shouldBe Month.MARCH
    }

    @Test
    fun `given id 4 then month is April`() {
        Month.from(4) shouldBe Month.APRIL
    }

    @Test
    fun `given id 5 then month is May`() {
        Month.from(5) shouldBe Month.MAY
    }

    @Test
    fun `given id 6 then month is June`() {
        Month.from(6) shouldBe Month.JUNE
    }

    @Test
    fun `given id 7 then month is July`() {
        Month.from(7) shouldBe Month.JULY
    }

    @Test
    fun `given id 8 then month is August`() {
        Month.from(8) shouldBe Month.AUGUST
    }

    @Test
    fun `given id 9 then month is September`() {
        Month.from(9) shouldBe Month.SEPTEMBER
    }

    @Test
    fun `given id 10 then month is Ocotober`() {
        Month.from(10) shouldBe Month.OCTOBER
    }

    @Test
    fun `given id 11 then month is November`() {
        Month.from(11) shouldBe Month.NOVEMBER
    }

    @Test
    fun `given id 12 then month is December`() {
        Month.from(12) shouldBe Month.DECEMBER
    }
}
