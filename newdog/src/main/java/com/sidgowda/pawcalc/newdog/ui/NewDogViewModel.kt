package com.sidgowda.pawcalc.newdog.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class NewDogViewModel @Inject constructor() : ViewModel() {

    private val nameInput = MutableStateFlow("")
    private val dateInput = MutableStateFlow("")
    private val weightInput = MutableStateFlow("")

    var name by mutableStateOf("")
        private set

    val nameHasError by derivedStateOf {
        // check if name is empty
    }

    var date by mutableStateOf("")
        private set

    val dateHasError by derivedStateOf {
        // check if date is correct
    }

    var weight by mutableStateOf("")
        private set

    val weightHasError by derivedStateOf {
        // check if weight has error
    }

    fun updateName(input: String) {
        this.name = input
    }

    fun updateDate(input: String) {
        this.date = input
    }

    fun updateWeight(input: String) {
        this.weight = input
    }
}
