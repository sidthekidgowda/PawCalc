package com.sidgowda.pawcalc.common.setting

enum class DateFormat(val index: Int) {
    AMERICAN(index = 0),
    INTERNATIONAL(index = 1);

    companion object {
        fun from(index: Int): DateFormat {
            return DateFormat.values().first { it.index == index }
        }
    }
}
