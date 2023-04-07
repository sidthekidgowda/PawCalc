package com.sidgowda.pawcalc.date

interface DatePickerListener {
    fun dateSelected(date: String)
    fun onCancel()

    fun onDismiss()
}
