package com.sidgowda.pawcalc.doginput.date

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
import com.sidgowda.pawcalc.data.date.calendar
import com.sidgowda.pawcalc.data.date.dateFromLong
import com.sidgowda.pawcalc.doginput.R
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class DatePickerDialogFragment : Fragment() {

    companion object {
        const val BUNDLE_DATE_KEY = "date"
        const val BUNDLE_IS_DATE_FORMAT_INTERNATIONAL = "date_format_international"
    }

    @Inject
    @Named("computation")
    lateinit var computationDispatcher: CoroutineDispatcher

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
                    // date should be passed in to minimize delay in using MaterialDatePicker to
                    // calculate time
                    val birthDate = checkNotNull(arguments?.getLong(BUNDLE_DATE_KEY))
                    showDatePickerDialog(birthDate)
                }
            }
        }
    }

    private fun calendarConstraints(): CalendarConstraints {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = calendar(today)
        return CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .setEnd(calendar.timeInMillis)
            .build()
    }

    private fun showDatePickerDialog(birthDate: Long) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(birthDate)
            .setTitleText(getString(R.string.date_picker_dialog_title))
            .setCalendarConstraints(calendarConstraints())
            .build()
            .apply {
                // dialog canceled with back button or touching scrim view
                addOnCancelListener {
                    Timber.d("Cancelled selecting birth date")
                    datePickerListener?.onCancel()
                }
                // ok button clicked
                addOnPositiveButtonClickListener { date ->
                    // convert date which is in milliseconds in a coroutine
                    convertDateFromLongAndSend(date)
                }
                // cancel button clicked
                addOnNegativeButtonClickListener {
                    Timber.d("Cancel button clicked")
                    datePickerListener?.onCancel()
                }
            }
        datePicker.show(childFragmentManager, DatePickerDialogFragment::class.java.simpleName)
    }

    private fun convertDateFromLongAndSend(date: Long) {
        viewLifecycleOwner.lifecycleScope.launch(computationDispatcher) {
            val isDateFormatInternational = arguments?.getBoolean(
                BUNDLE_IS_DATE_FORMAT_INTERNATIONAL
            ) ?: false
            val birthDate: String = dateFromLong(date, isDateFormatInternational)
            withContext(Dispatchers.Main.immediate) {
                Timber.d("Selected date: $birthDate")
                datePickerListener?.dateSelected(birthDate)
            }
        }
    }

}
