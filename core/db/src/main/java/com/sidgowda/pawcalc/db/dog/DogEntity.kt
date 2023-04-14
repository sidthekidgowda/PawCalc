package com.sidgowda.pawcalc.db.dog

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dogs")
data class DogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val weight: Double,
    val birthDate: String,
    val profilePic: Uri
)
