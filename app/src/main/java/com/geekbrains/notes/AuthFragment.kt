package com.geekbrains.notes

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private val model: MainViewModel by activityViewModels()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            if (task.isSuccessful) {
                model.setUserId(task.result.email!!)
                model.load()
                model.setInterfaceState(MainViewModel.InterfaceState.SHOW_LIST)
            } else {
                Toast.makeText(context, "auth failed: ${task.exception.toString()}", Toast.LENGTH_LONG)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(requireContext(), signInOptions)

        view.findViewById<SignInButton>(R.id.auth_sign_in_button).setOnClickListener {
            resultLauncher.launch(client.signInIntent)
        }
    }
}