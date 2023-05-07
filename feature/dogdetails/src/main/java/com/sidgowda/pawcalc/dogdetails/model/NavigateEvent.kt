package com.sidgowda.pawcalc.dogdetails.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class NavigateEvent : Parcelable {

    @Parcelize
    data class EditDog(val id: Int) : NavigateEvent()
}
