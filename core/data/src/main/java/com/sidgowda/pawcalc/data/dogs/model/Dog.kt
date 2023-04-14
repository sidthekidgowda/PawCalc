package com.sidgowda.pawcalc.data.dogs.model

import android.net.Uri

data class Dog(
    val id: Int,
    val profilePic: Uri,
    val name: String,
    val weight: Double,
    val birthDate: String,
    val dogYears: String,
    val humanYears: String,
    val isLoading: Boolean
)
