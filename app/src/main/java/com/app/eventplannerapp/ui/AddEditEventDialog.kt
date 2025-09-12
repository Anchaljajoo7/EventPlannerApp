package com.app.eventplannerapp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.app.eventplannerapp.R
import java.util.Calendar

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

        etTitle.setText(initialTitle ?: "")
        etDescription.setText(initialDescription ?: "")

        var chosenTimeMillis = initialTimeMillis ?: System.currentTimeMillis()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (initialTitle == null) R.string.add_event else R.string.edit_event)
            .setView(view)
            .setPositiveButton(R.string.pick_time) { _, _ ->
                val cal = Calendar.getInstance().apply { timeInMillis = chosenTimeMillis }
                val picker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(cal.get(Calendar.HOUR_OF_DAY))
                    .setMinute(cal.get(Calendar.MINUTE))
                    .build()
                picker.addOnPositiveButtonClickListener {
                    val fm = activity?.supportFragmentManager ?: return@addOnPositiveButtonClickListener
                    cal.set(Calendar.HOUR_OF_DAY, picker.hour)
                    cal.set(Calendar.MINUTE, picker.minute)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    chosenTimeMillis = cal.timeInMillis

                    // Ensure current dialog is closed before showing a new one
                    dismissAllowingStateLoss()
                    fm.executePendingTransactions()

                    // Avoid duplicate existing instance with same tag
                    (fm.findFragmentByTag("AddEditEventDialog") as? DialogFragment)?.dismissAllowingStateLoss()
                    fm.executePendingTransactions()

                    // Re-open dialog for confirm with updated time
                    AddEditEventDialog(
                        etTitle.text.toString(),
                        etDescription.text.toString(),
                        chosenTimeMillis,
                        onConfirm
                    ).show(fm, "AddEditEventDialog")
                }
                activity?.supportFragmentManager?.let { picker.show(it, "timePicker") }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setNeutralButton(R.string.save) { _, _ ->
                onConfirm(etTitle.text.toString(), etDescription.text.toString(), chosenTimeMillis)
            }
            .create()

        return dialog
    }
}

