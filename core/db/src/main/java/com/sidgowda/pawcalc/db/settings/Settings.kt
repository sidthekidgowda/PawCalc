package com.sidgowda.pawcalc.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Settings(
    @PrimaryKey val id: Int,
    val theme: Theme,
    val dateFormat: DateFormat,
    val weightFormat: WeightFormat
)
