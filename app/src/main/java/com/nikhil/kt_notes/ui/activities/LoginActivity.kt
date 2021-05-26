package com.nikhil.kt_notes.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.nikhil.kt_notes.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin: MaterialButton
    private lateinit var elEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var elPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var tvRegisterScreen: TextView
    private lateinit var auth: FirebaseAuth


    override fun onStart() {
        super.onStart()
        auth.signOut()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        initialization()
        btnLogin.setOnClickListener {
            signupUser()
        }

        tvRegisterScreen.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initialization() {
        btnLogin = findViewById(R.id.btnLogin)
        elEmail = findViewById(R.id.elEmail)
        etEmail = findViewById(R.id.etEmail)
        elPassword = findViewById(R.id.elPassword)
        etPassword = findViewById(R.id.etPassword)
        tvRegisterScreen = findViewById(R.id.tvRegisterScreen)
    }

    private fun signupUser() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.trim().isNotEmpty()) {
            if (password.trim().isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.signInWithEmailAndPassword(email, password).await()
                        withContext(Dispatchers.Main) {
                            Intent(this@LoginActivity, NotesActivity::class.java).also {
                                startActivity(it)
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            } else {
                elPassword.error = "Password Invalid!"
            }
        } else {
            elEmail.error = "Please enter a valid Email."
        }
    }
}
