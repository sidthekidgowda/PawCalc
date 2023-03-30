package com.sidgowda.pawcalc.doginput.model

data class DogInputState(
    val profilePicInput: String = "",
    val nameInput: String = "",
    val weightInput: String = "",
    val birthDateInput: String = "",
    val inputRequirements: List<DogInputRequirements> = emptyList(),
    val inputMode: DogInputMode = DogInputMode.NEW_DOG,
    val isError: Boolean = false
) {
    fun isInputValid(): Boolean {
        return nameInput.isNotEmpty() &&
                weightInput.isNotEmpty() &&
                birthDateInput.isNotEmpty() &&
                profilePicInput.isNotEmpty() &&
                inputRequirements.containsAll(DogInputRequirements.values().toList())
    }
}
