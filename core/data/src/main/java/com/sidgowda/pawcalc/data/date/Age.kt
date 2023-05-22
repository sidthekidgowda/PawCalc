package com.sidgowda.pawcalc.data.date

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Age(
    val years: Int,
    val months: Int,
    val days: Int
) : Parcelable
