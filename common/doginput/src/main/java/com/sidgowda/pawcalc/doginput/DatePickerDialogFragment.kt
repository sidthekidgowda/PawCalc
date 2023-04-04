package com.sidgowda.pawcalc.doginput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.datepicker.MaterialDatePicker

class DatePickerDialogFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.date_picker_dialog_layout, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("It works")
            .build()
        datePicker.show(childFragmentManager, "tag")
    }
}
