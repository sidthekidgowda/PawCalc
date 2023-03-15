package com.sidgowda.pawcalc.newdog.ui

data class DogInfoInput(
    val name: String = "",
    val validName: Boolean = true,
    val date: String = "",
    val validDate: Boolean = true,
    val weight: String = "",
    val validWeight: Boolean = true
)
