package com.sidgowda.pawcalc.common.settings

import io.kotest.matchers.shouldBe
import org.junit.Test

class ThemeFormatTest {

    @Test
    fun `index 0 should return ThemeFormat System`() {
        ThemeFormat.from(0) shouldBe ThemeFormat.SYSTEM
    }

    @Test
    fun `index 1 should return ThemeFormat Dark`() {
        ThemeFormat.from(1) shouldBe ThemeFormat.DARK
    }

    @Test
    fun `index 2 should return ThemeFormat Light`() {
        ThemeFormat.from(2) shouldBe ThemeFormat.LIGHT
    }

    @Test(expected = NoSuchElementException::class)
    fun `index 4 should throw NoSuchElementException`() {
        ThemeFormat.from(4)
    }
}
