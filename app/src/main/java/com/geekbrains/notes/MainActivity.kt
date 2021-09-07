package com.geekbrains.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    var isPortrait: Boolean = true
    val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isPortrait = resources.getBoolean(R.bool.isPortrait)
        model.selectedNoteId.observe(this, { id -> showDetailsFragment() })
        model.removedItem.observe(this, { id -> showListFragment() })
        model.modifiedNoteId.observe(this, { id -> showListFragment() })
        setContentView(R.layout.activity_main)
        findViewById<Toolbar>(R.id.main_toolbar).setOnMenuItemClickListener({ item -> handleMenu(item) })
        if (isPortrait) {
            supportFragmentManager.beginTransaction().hide(detailFrag()).show(listFrag()).commit()
        } else {
            supportFragmentManager.beginTransaction().show(detailFrag()).show(listFrag()).commit()
        }
    }

    fun showDetailsFragment() {
        if (isPortrait) {
            supportFragmentManager.beginTransaction().hide(listFrag()).show(detailFrag()).commit()
        }
    }

    fun showListFragment() {
        if (isPortrait) {
            supportFragmentManager.beginTransaction().hide(detailFrag()).show(listFrag()).commit()
        }
    }

    fun listFrag(): Fragment = supportFragmentManager.findFragmentByTag("list_fragment_tag")!!
    fun detailFrag(): Fragment = supportFragmentManager.findFragmentByTag("details_fragment_tag")!!

    fun handleMenu(item: MenuItem): Boolean {
        if (item.itemId == R.id.about) {
            Toast.makeText(this, R.string.unimplemented, Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }
}