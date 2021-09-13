package com.geekbrains.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceControl
import androidx.activity.viewModels
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    var isPortrait: Boolean = true
    val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isPortrait = resources.getBoolean(R.bool.isPortrait)
        model.interfaceState.observe(this) { state -> handleStateChange(state) }
        setContentView(R.layout.activity_main)

        // приводим интерфейс в изначальное состояние
        if (isPortrait) {
            handleStateChange(model.interfaceState.value!!)
        } else {
            supportFragmentManager.beginTransaction().show(detailFrag()).show(listFrag()).commit()
        }
    }

    private fun handleStateChange(state: MainViewModel.InterfaceState) {
        // только в портретном режиме надо переключать отображаемый фрагмент
        if (isPortrait) {
            val t = supportFragmentManager.beginTransaction()
            when (state) {
                MainViewModel.InterfaceState.SHOW_LIST -> t.hide(detailFrag()).show(listFrag())
                MainViewModel.InterfaceState.SHOW_DETAILS -> t.hide(listFrag()).show(detailFrag())
            }.commit()
        }
    }

    private fun listFrag(): Fragment =
        supportFragmentManager.findFragmentByTag("list_fragment_tag")!!

    private fun detailFrag(): Fragment =
        supportFragmentManager.findFragmentByTag("details_fragment_tag")!!
}