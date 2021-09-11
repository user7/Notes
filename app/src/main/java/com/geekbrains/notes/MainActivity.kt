package com.geekbrains.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

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

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setOnMenuItemClickListener { item -> handleMenu(item) }
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            R.string.drawer_open_label,
            R.string.drawer_close_label
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val nav = findViewById<NavigationView>(R.id.navigation_view).setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.drawer_about -> unimplemented()
                R.id.drawer_help -> unimplemented()
                else -> false
            }
        }

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

    fun unimplemented(): Boolean {
        Toast.makeText(this, R.string.unimplemented, Toast.LENGTH_SHORT).show()
        return true
    }

    fun handleMenu(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.about -> unimplemented()
            else -> false
        }
}