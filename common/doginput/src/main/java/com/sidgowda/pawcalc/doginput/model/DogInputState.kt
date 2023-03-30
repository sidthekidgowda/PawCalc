package com.sidgowda.pawcalc.doginput.model

data class DogInputState(
    val profilePicInput: String = "",
    val name: String = "",
    val weight: String = "",
    val birthDate: String = "",
    val inputRequirements: List<DogInputRequirements> = emptyList(),
    val inputMode: DogInputMode = DogInputMode.NEW_DOG,
    val isError: Boolean = false
) {
    fun isInputValid(): Boolean {
        return name.isNotEmpty() &&
                weight.isNotEmpty() &&
                birthDate.isNotEmpty() &&
                profilePicInput.isNotEmpty() &&
                inputRequirements.containsAll(DogInputRequirements.values().toList())
    }
}
