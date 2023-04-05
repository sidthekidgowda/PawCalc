package com.sidgowda.pawcalc.doginput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
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
                    val birthDate = arguments?.let { bundle ->
                        bundle.getString("Date")
                    } ?: ""
                    showDatePickerDialog(birthDate)
                }
            }
        }
    }

    private fun showDatePickerDialog(birthDate: String) {
        val date: Long = dateToLong(birthDate)
        val selectedDate = if (date == 0L) {
            MaterialDatePicker.todayInUtcMilliseconds()
        } else {
            date
        }
        // set selection to date sent
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(selectedDate)
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
                    // convert date which is in milliseconds in a coroutine
                    convertDateFromLongAndSend(date)
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

    private fun dateFromLong(date: Long): String {
        //this works as push
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = date
        val simpleDate = SimpleDateFormat("MM-dd-yyyy")
        simpleDate.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDate.format(calendar.timeInMillis)
    }

    private fun dateToLong(date: String): Long {
        if (date.isEmpty()) return 0
        val simpleDate = SimpleDateFormat("MM-dd-yyyy")
        simpleDate.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date? = try {
            simpleDate.parse(date)
        } catch (e: Exception) {
            null
        }
        return if (date != null) {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = date.time
            calendar.timeInMillis
        } else {
            0
        }
    }

    private fun convertDateFromLongAndSend(date: Long) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            val birthDate: String = dateFromLong(date)
            withContext(Dispatchers.Main.immediate) {
                datePickerListener?.dateSelected(birthDate)
            }
        }
    }

}
