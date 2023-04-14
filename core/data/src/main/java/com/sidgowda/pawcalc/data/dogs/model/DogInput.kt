package com.sidgowda.pawcalc.data.dogs.model

import android.net.Uri

data class DogInput(
    val profilePic: Uri,
    val name: String,
    val weight: String,
    val birthDate: String
)
