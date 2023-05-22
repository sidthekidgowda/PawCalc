package com.sidgowda.pawcalc.doginput.date

interface DatePickerListener {
    fun dateSelected(date: String)
    fun onCancel()
    fun onDismiss()
}
