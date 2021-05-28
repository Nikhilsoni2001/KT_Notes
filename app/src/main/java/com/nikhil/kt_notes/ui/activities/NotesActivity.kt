package com.nikhil.kt_notes.ui.activities

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesActivity : AppCompatActivity() {

    lateinit var viewModel: NotesViewModel
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    companion object {
        val collection_name = FirebaseAuth.getInstance().currentUser?.email
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val database = NotesDatabase(this@NotesActivity)
        val repository = NotesRepository(database)
        val factory = NotesViewModelFactory(this@NotesActivity, repository)
        viewModel = ViewModelProvider(this, factory).get(NotesViewModel::class.java)

        checkTheme()

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
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteAllNotes()
            FirebaseAuth.getInstance().signOut()
            withContext(Dispatchers.Main) {
                Intent(this@NotesActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
                finish()
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()
                Toast.makeText(this@NotesActivity, "Looking for $query", Toast.LENGTH_SHORT).show()
                searchDB(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(this@NotesActivity, "Looking for $newText", Toast.LENGTH_SHORT)
                    .show()
                searchDB(newText.toString())
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun searchDB(query: String) {
        val searchQuery = "%$query%"
        viewModel.searchDatabase(searchQuery)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {
            R.id.theme -> {
                chooseThemeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun chooseThemeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_theme_text))
        val styles = arrayOf("Light", "Dark", "System Default")
        val intChecked = viewModel.getDark()

        builder.setSingleChoiceItems(styles, intChecked) { dialog, mode ->
            when (mode) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    viewModel.setDark(0)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }

                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    viewModel.setDark(1)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    viewModel.setDark(2)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun checkTheme() {
        when (viewModel.getDark()) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }
}