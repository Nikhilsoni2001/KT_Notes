package com.nikhil.kt_notes.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nikhil.kt_notes.R
import com.nikhil.kt_notes.db.NotesDatabase
import com.nikhil.kt_notes.repositories.NotesRepository
import com.nikhil.kt_notes.ui.fragments.FavouriteFragment
import com.nikhil.kt_notes.ui.fragments.NotesFragment
import com.nikhil.kt_notes.ui.viewModel.NotesViewModel
import com.nikhil.kt_notes.ui.viewModel.NotesViewModelFactory

class NotesActivity : AppCompatActivity() {

    lateinit var viewModel: NotesViewModel
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val database = NotesDatabase(this@NotesActivity)
        val repository = NotesRepository(database)
        val factory = NotesViewModelFactory(this@NotesActivity, repository)
        viewModel = ViewModelProvider(this, factory).get(NotesViewModel::class.java)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> openHome()
                R.id.miFavourites -> openFavourites()
                R.id.miInfo -> Toast.makeText(
                    applicationContext,
                    "App made by Ishant, Nikhil and Aditya",
                    Toast.LENGTH_LONG
                ).show()
                R.id.miSignOut -> signOut()
            }
            true
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Intent(this@NotesActivity, LoginActivity::class.java).also {
            startActivity(it)
        }
        finish()
    }

    private fun openHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, NotesFragment())
            .commit()
    }

    private fun openFavourites() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, FavouriteFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}