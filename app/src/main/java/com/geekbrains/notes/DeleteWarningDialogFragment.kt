package com.geekbrains.notes

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class DeleteWarningDialogFragment : DialogFragment() {
    companion object {
        const val ISYES = "y"
        const val KEY = "DeleteWarningDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.yes) { _, _ -> setResultYes(true) }
            .setNegativeButton(R.string.no) { _, _ -> setResultYes(false) }
            .setOnDismissListener { setResultYes(false) }
            .setTitle(R.string.confirm_delete)
            .create()
    }

    private fun setResultYes(yes: Boolean) = setFragmentResult(KEY, bundleOf(ISYES to yes))
}