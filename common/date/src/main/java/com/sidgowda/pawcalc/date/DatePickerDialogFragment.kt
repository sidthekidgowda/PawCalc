package com.sidgowda.pawcalc.date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class DatePickerDialogFragment : Fragment() {

    companion object {
        const val BUNDLE_DATE_KEY = "date"
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
                    // date needs to be passed in to minimize delay in calculating time
                    val birthDate = arguments?.getLong(BUNDLE_DATE_KEY) ?: MaterialDatePicker.todayInUtcMilliseconds()
                    showDatePickerDialog(birthDate)
                }
            }
        }
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

    private fun convertDateFromLongAndSend(date: Long) {
        viewLifecycleOwner.lifecycleScope.launch(computationDispatcher) {
            val birthDate: String = dateFromLong(date)
            withContext(Dispatchers.Main.immediate) {
                datePickerListener?.dateSelected(birthDate)
            }
        }
    }

}
