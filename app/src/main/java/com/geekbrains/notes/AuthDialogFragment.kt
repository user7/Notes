package com.geekbrains.notes

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class AuthDialogFragment : DialogFragment() {
    private val model: MainViewModel by activityViewModels()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            val email = task.result?.email
            if (task.isSuccessful && email != null) {
                model.setUserId(email)
                model.load()
                model.setInterfaceState(MainViewModel.InterfaceState.SHOW_LIST)
                dismiss()
            } else {
                Toast.makeText(context, "auth failed: ${task.exception.toString()}", Toast.LENGTH_LONG)
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.fragment_auth, null)
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(requireContext(), signInOptions)
        view.findViewById<View>(R.id.auth_sign_in_button).setOnClickListener {
            resultLauncher.launch(client.signInIntent)
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.sign_in_dialog_title)
            .setView(view)
            .show()
        return dialog
    }
}