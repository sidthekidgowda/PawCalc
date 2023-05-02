package com.sidgowda.pawcalc.common.settings

import io.kotest.matchers.shouldBe
import org.junit.Test

class DateFormatTest {

    @Test
    fun `index 0 should return DateFormat American`() {
        DateFormat.from(index = 0) shouldBe DateFormat.AMERICAN
    }

    @Test
    fun `index 1 should return DateFormat International`() {
        DateFormat.from(index = 1) shouldBe DateFormat.INTERNATIONAL
    }

    @Test(expected = NoSuchElementException::class)
    fun `index 2 should throw NoSuchElementException`() {
        DateFormat.from(index = 2)
    }
}
