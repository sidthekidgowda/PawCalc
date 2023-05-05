package com.sidgowda.pawcalc.data.dogs.model

import android.net.Uri
import android.os.Parcelable
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.Age
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dog(
    val id: Int,
    val profilePic: Uri,
    val name: String,
    val weight: Double,
    val weightFormat: WeightFormat,
    val birthDate: String,
    val dateFormat: DateFormat,
    val dogYears: Age,
    val humanYears: Age
) : Parcelable

