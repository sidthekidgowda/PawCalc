package com.sidgowda.pawcalc.doglist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class NavigateEvent : Parcelable {
    @Parcelize
    object AddDog : NavigateEvent()
    @Parcelize
    data class DogDetails(val id: Int) : NavigateEvent()
}
