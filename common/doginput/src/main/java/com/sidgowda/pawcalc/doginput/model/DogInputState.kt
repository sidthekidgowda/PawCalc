package com.sidgowda.pawcalc.doginput.model

data class DogInputState(
    val profilePicInput: String = "",
    val nameInput: String = "",
    val weightInput: String = "",
    val dateInput: String = "",
    val inputRequirements: List<DogInputRequirements> = emptyList(),
    val inputMode: DogInputMode = DogInputMode.NEW_DOG,
) {
    fun isInputValid(): Boolean {
        return nameInput.isNotEmpty() &&
                weightInput.isNotEmpty() &&
                dateInput.isNotEmpty() &&
                profilePicInput.isNotEmpty() &&
                inputRequirements.containsAll(DogInputRequirements.values().toList())
    }
}
