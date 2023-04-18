package com.sidgowda.pawcalc.data

import com.sidgowda.pawcalc.data.date.Age
import com.sidgowda.pawcalc.data.date.toDogYears
import io.kotest.matchers.shouldBe
import org.junit.Test

class AgeCalculatorTest {

    @Test
    fun test() {
        val age = "12/20/1990".toDogYears()

        age shouldBe Age(
            32,
            3,
            28
        )
    }

    @Test
    fun test2() {
        val age = "7/30/2019".toDogYears()

        age shouldBe Age(
            3,
            8,
            18
        )
    }

    @Test
    fun test3() {
        val age = "2/29/2020".toDogYears()

        age shouldBe Age(
            3,
            1,
            19
        )
    }

    @Test
    fun test4() {
        val age = "5/31/2009".toDogYears()

        age shouldBe Age(
            13,
            10,
            17
        )
    }

    @Test
    fun test5() {
        val age = "3/30/1993".toDogYears()

        age shouldBe Age(
            30,
            0,
            18
        )
    }

    @Test
    fun test6() {
        val age = "3/17/2023".toDogYears()

        age shouldBe Age(
            0,
            1,
            0
        )
    }


    @Test
    fun test7() {
        val age = "4/17/2023".toDogYears()

        age shouldBe Age(
            0,
            0,
            0
        )
    }

    @Test
    fun test8() {
        val age = "4/17/2023".toDogYears()

        age shouldBe Age(
            0,
            0,
            0
        )
    }

    @Test
    fun test9() {
        val age = "3/17/2023".toDogYears()

        age shouldBe Age(
            0,
            1,
            0
        )
    }

    @Test
    fun test10() {
        val age = "3/31/2023".toDogYears()

        age shouldBe Age(
            0,
            0,
            17
        )
    }

    @Test
    fun test11() {
        val age = "4/16/2023".toDogYears()

        age shouldBe Age(
            0,
            0,
            1
        )
    }


    @Test
    fun test911() {
        val age = "9/11/2001".toDogYears()

        age shouldBe Age(
            21,
            7,
            6
        )
    }

    @Test
    fun test22004() {
        val age = "2/6/2004".toDogYears()

        age shouldBe Age(
            19,
            2,
            11
        )
    }

    @Test
    fun test21004() {
        val age = "2/20/2004".toDogYears(today = "2/6/2004")

        age shouldBe Age(
            0,
            0,
            14
        )
    }

    @Test
    fun test21x004() {
        val age = "9/15/2010".toDogYears(today = "10/15/2010")

        age shouldBe Age(
            0,
            1,
            0
        )
    }
}
