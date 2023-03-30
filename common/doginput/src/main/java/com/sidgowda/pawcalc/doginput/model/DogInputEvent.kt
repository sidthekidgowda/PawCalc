package com.sidgowda.pawcalc.doginput.model

sealed class DogInputEvent {
    class NameChanged(val name: String) : DogInputEvent()
    class WeightChanged(val weight: String): DogInputEvent()
    class BirthDateChanged(val date: String): DogInputEvent()
    object SavingInfo : DogInputEvent()
    object ErrorDismissed : DogInputEvent()
}
