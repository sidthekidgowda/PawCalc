package com.sidgowda.pawcalc.doginput.model

import android.net.Uri

sealed class DogInputEvent {
    class PicChanged(val pictureUrl: Uri?) : DogInputEvent()
    class NameChanged(val name: String) : DogInputEvent()
    class WeightChanged(val weight: String) : DogInputEvent()
    class BirthDateChanged(val birthDate: String) : DogInputEvent()
    object ErrorDismissed : DogInputEvent()
    object SavingInfo : DogInputEvent()
}
