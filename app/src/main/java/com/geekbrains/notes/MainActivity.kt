package com.geekbrains.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment

fun d(str: String) {
    Log.d("++", str)
}

class MainActivity : AppCompatActivity() {
    var isPortrait: Boolean = true
    val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isPortrait = resources.getBoolean(R.bool.isPortrait)
        model.interfaceState.observe(this) { state -> handleStateChange(state) }
        setContentView(R.layout.activity_main)

        handleStateChange(model.interfaceState.value!!)
    }

    override fun onBackPressed() {
        if (model.interfaceState.value == MainViewModel.InterfaceState.SHOW_DETAILS) {
            model.cancelEditing()
        } else {
            finish()
        }
    }

    private fun handleStateChange(state: MainViewModel.InterfaceState) {
        if (isPortrait) {
            val t = supportFragmentManager.beginTransaction()
            when (state) {
                MainViewModel.InterfaceState.SHOW_LIST -> t.hide(authFrag()).hide(detailFrag()).show(listFrag())
                MainViewModel.InterfaceState.SHOW_DETAILS -> t.hide(authFrag()).hide(listFrag()).show(detailFrag())
                MainViewModel.InterfaceState.SHOW_AUTH -> t.show(authFrag()).hide(listFrag()).hide(detailFrag())
            }.commit()
        } else {
            val t = supportFragmentManager.beginTransaction()
            when (state) {
                MainViewModel.InterfaceState.SHOW_LIST -> t.hide(authFrag()).show(detailFrag()).show(listFrag())
                MainViewModel.InterfaceState.SHOW_DETAILS -> t.hide(authFrag()).show(listFrag()).show(detailFrag())
                MainViewModel.InterfaceState.SHOW_AUTH -> t.show(authFrag()).hide(listFrag()).hide(detailFrag())
            }.commit()
        }
    }

    private fun listFrag(): Fragment =
        supportFragmentManager.findFragmentByTag("list_fragment_tag")!!

    private fun detailFrag(): Fragment =
        supportFragmentManager.findFragmentByTag("details_fragment_tag")!!

    private fun authFrag(): Fragment =
        supportFragmentManager.findFragmentByTag("auth_fragment_tag")!!
}