package com.sidgowda.pawcalc.data

import com.sidgowda.pawcalc.data.date.Age
import com.sidgowda.pawcalc.data.date.toDogYears
import io.kotest.matchers.shouldBe
import org.junit.Test

class AgeCalculatorTest {

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
    fun test10() {
        val age = "3/31/2023".toDogYears()

        age shouldBe Age(
            0,
            0,
            17
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



}
