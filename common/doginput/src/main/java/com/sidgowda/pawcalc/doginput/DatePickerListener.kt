package com.sidgowda.pawcalc.doginput

interface DatePickerListener {

    fun dateSelected(date: String)

    fun onCancel()
    fun onDismiss()
}
