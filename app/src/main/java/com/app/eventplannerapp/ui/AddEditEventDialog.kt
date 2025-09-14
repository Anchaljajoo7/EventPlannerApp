package com.app.eventplannerapp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.app.eventplannerapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditEventDialog(
    private val initialTitle: String? = null,
    private val initialDescription: String? = null,
    private val initialTimeMillis: Long? = null,
    private val onConfirm: (title: String, description: String, timeMillis: Long) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_edit_event, null, false)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val tvSelectedDate = view.findViewById<TextView>(R.id.tvSelectedDate)
        val tvSelectedTime = view.findViewById<TextView>(R.id.tvSelectedTime)

        etTitle.setText(initialTitle ?: "")
        etDescription.setText(initialDescription ?: "")

        var chosenTimeMillis = initialTimeMillis ?: System.currentTimeMillis()
        
        // Update the date and time displays
        fun updateDateDisplay() {
            val dateString = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(chosenTimeMillis)
            tvSelectedDate.text = "Selected Date: $dateString"
        }
        
        fun updateTimeDisplay() {
            val timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(chosenTimeMillis)
            tvSelectedTime.text = "Selected Time: $timeString"
        }
        
        updateDateDisplay()
        updateTimeDisplay()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (initialTitle == null) R.string.add_event else R.string.edit_event)
            .setView(view)
            .setPositiveButton(R.string.save) { _, _ ->
                onConfirm(etTitle.text.toString(), etDescription.text.toString(), chosenTimeMillis)
            }
            .setNeutralButton(R.string.pick_date) { _, _ ->
                // Don't dismiss the dialog, just show the date picker
            }
            .setNegativeButton(R.string.pick_time) { _, _ ->
                // Don't dismiss the dialog, just show the time picker
            }
            .create()

        // Override the button click listeners to prevent dialog dismissal
        dialog.setOnShowListener {
            val pickDateButton = dialog.getButton(Dialog.BUTTON_NEUTRAL)
            val pickTimeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE)
            
            pickDateButton.setOnClickListener {
                // Create and show date picker
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(chosenTimeMillis)
                    .build()
                
                datePicker.addOnPositiveButtonClickListener { selectedDate ->
                    // Update the chosen time with the selected date (keeping the same time)
                    val selectedCal = Calendar.getInstance().apply { timeInMillis = chosenTimeMillis }
                    val newCal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                    
                    // Keep the original time but update the date
                    selectedCal.set(Calendar.YEAR, newCal.get(Calendar.YEAR))
                    selectedCal.set(Calendar.MONTH, newCal.get(Calendar.MONTH))
                    selectedCal.set(Calendar.DAY_OF_MONTH, newCal.get(Calendar.DAY_OF_MONTH))
                    
                    chosenTimeMillis = selectedCal.timeInMillis
                    updateDateDisplay()
                    updateTimeDisplay()
                    
                    // Debug log
                    android.util.Log.d("AddEditEventDialog", "Date selected: $chosenTimeMillis")
                }
                
                // Show the date picker
                datePicker.show(parentFragmentManager, "datePicker")
            }
            
            pickTimeButton.setOnClickListener {
                val cal = Calendar.getInstance().apply { timeInMillis = chosenTimeMillis }
                val picker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(cal.get(Calendar.HOUR_OF_DAY))
                    .setMinute(cal.get(Calendar.MINUTE))
                    .build()
                picker.addOnPositiveButtonClickListener {
                    // Update the chosen time with the selected time
                    val selectedCal = Calendar.getInstance().apply { timeInMillis = chosenTimeMillis }
                    selectedCal.set(Calendar.HOUR_OF_DAY, picker.hour)
                    selectedCal.set(Calendar.MINUTE, picker.minute)
                    selectedCal.set(Calendar.SECOND, 0)
                    selectedCal.set(Calendar.MILLISECOND, 0)
                    chosenTimeMillis = selectedCal.timeInMillis

                    // Update both displays
                    updateDateDisplay()
                    updateTimeDisplay()
                    
                    // Debug log
                    android.util.Log.d("AddEditEventDialog", "Time selected: $chosenTimeMillis")
                }
                // Show the time picker
                picker.show(parentFragmentManager, "timePicker")
            }
        }

        return dialog
    }
}

