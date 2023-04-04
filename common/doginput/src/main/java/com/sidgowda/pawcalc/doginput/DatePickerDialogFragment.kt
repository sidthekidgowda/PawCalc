package com.sidgowda.pawcalc.doginput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import java.util.*

internal class DatePickerDialogFragment : Fragment() {

    var datePickerListener: DatePickerListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PawCalcTheme {
                    showDatePickerDialog()
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText(getString(R.string.date_picker_dialog_title))
            .setCalendarConstraints(calendarConstraints())
            .build()
            .apply {
                // dialog canceled with back button or touching scrim view
                addOnCancelListener {
                    datePickerListener?.onCancel()
                }
                // ok button clicked
                addOnPositiveButtonClickListener { date ->
                    datePickerListener?.dateSelected(date.toString())
                }
                // cancel button clicked
                addOnNegativeButtonClickListener {
                    datePickerListener?.onCancel()
                }
            }
        datePicker.show(childFragmentManager, DatePickerDialogFragment::class.java.simpleName)
    }

    private fun calendarConstraints(): CalendarConstraints{
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = today
       return CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .setEnd(calendar.timeInMillis)
            .build()
    }
}
