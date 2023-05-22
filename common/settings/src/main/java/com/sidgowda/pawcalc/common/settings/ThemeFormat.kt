package com.sidgowda.pawcalc.common.settings

enum class ThemeFormat(val index: Int) {
    SYSTEM(index = 0),
    DARK(index = 1),
    LIGHT(index = 2);

    companion object {
        fun from(index: Int): ThemeFormat {
            return ThemeFormat.values().first { it.index == index }
        }
    }
}
