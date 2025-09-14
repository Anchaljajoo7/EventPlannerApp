package com.app.eventplannerapp.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.app.eventplannerapp.R
import com.app.eventplannerapp.databinding.DialogAddEditEventBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
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
        val binding = DialogAddEditEventBinding.inflate(LayoutInflater.from(requireContext()))

        binding.etTitle.setText(initialTitle ?: "")
        binding.etDescription.setText(initialDescription ?: "")

        var chosenTimeMillis = initialTimeMillis ?: System.currentTimeMillis()

        // Update the date and time displays
        fun updateDateDisplay() {
            val dateString = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(chosenTimeMillis)
            binding.tvSelectedDate.text = "Selected Date: $dateString"
        }

        fun updateTimeDisplay() {
            val timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(chosenTimeMillis)
            binding.tvSelectedTime.text = "Selected Time: $timeString"
        }

        updateDateDisplay()
        updateTimeDisplay()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (initialTitle == null) R.string.add_event else R.string.edit_event)
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
//                onConfirm(binding.etTitle.text.toString(), binding.etDescription.text.toString(), chosenTimeMillis)
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
            val saveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

            saveButton.setOnClickListener {
                val title = binding.etTitle.text.toString().trim()
                val description = binding.etDescription.text.toString().trim()

                var isValid = true

                if (title.isEmpty()) {
                    binding.etTitle.error = "Required field"
                    isValid = false
                }

                if (description.isEmpty()) {
                    binding.etDescription.error = "Required field"
                    isValid = false
                }

                if (isValid) {
                    onConfirm(title, description, chosenTimeMillis)
                    dialog.dismiss()
                }
            }


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
                    Log.d("AddEditEventDialog", "Date selected: $chosenTimeMillis")
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
                    Log.d("AddEditEventDialog", "Time selected: $chosenTimeMillis")
                }
                // Show the time picker
                picker.show(parentFragmentManager, "timePicker")
            }
        }

        return dialog
    }
}