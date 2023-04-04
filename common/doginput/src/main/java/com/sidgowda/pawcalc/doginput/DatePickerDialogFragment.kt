package com.sidgowda.pawcalc.doginput

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

internal class DatePickerDialogFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PawCalcTheme {
                    DatePicker()
                }
            }
        }
    }

    @Composable
    fun DatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText("It works")
            .setCalendarConstraints(calendarContraints())
            .build()
        datePicker.show(childFragmentManager, "tag")
    }

    private fun calendarContraints(): CalendarConstraints {
        // todo figure out right time and calendar library
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = today

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setEnd(calendar.timeInMillis)
        return constraintsBuilder.build()
    }
}
