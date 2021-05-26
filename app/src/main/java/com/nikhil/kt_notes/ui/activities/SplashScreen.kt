package com.nikhil.kt_notes.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.nikhil.kt_notes.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this@SplashScreen, LoginActivity::class.java)
            GlobalScope.launch {
                delay(3000)
                startActivity(intent)
                finish()
            }
        } else {
            val intent = Intent(this@SplashScreen, NotesActivity::class.java)
            GlobalScope.launch {
                delay(3000)
                startActivity(intent)
                finish()
            }
        }
    }
}