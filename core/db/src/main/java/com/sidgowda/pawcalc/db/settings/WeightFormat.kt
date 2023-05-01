package com.sidgowda.pawcalc.db.settings

enum class WeightFormat(val index: Int) {
    POUNDS(index = 0),
    KILOGRAMS(index = 1);

    companion object {
        fun from(index: Int): WeightFormat {
            return WeightFormat.values().first { it.index == index }
        }
    }
}
