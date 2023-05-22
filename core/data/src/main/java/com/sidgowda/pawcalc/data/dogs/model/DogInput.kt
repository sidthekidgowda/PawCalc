package com.sidgowda.pawcalc.data.dogs.model

import android.net.Uri
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat

data class DogInput(
    val profilePic: Uri,
    val name: String,
    val weight: String,
    val weightFormat: WeightFormat,
    val birthDate: String,
    val dateFormat: DateFormat
)
