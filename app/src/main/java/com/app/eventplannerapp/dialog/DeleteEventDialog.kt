package com.app.eventplannerapp.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.app.eventplannerapp.R
import com.app.eventplannerapp.data.entity.EventEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeleteEventDialog(
    private val event: EventEntity,
    private val onConfirm: (EventEntity) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_event)
            .setMessage(R.string.delete_event_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                onConfirm(event)
            }
            .setNegativeButton(R.string.no, null)
            .create()
    }
}