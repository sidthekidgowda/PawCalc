package com.sidgowda.pawcalc.db.dog

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat

@Entity(tableName = "dogs")
data class DogEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val weight: Double,
    val weightFormat: WeightFormat,
    val birthDate: String,
    val dateFormat: DateFormat,
    val profilePic: Uri
)
