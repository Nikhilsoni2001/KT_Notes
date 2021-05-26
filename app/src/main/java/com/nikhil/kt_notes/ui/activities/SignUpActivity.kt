package com.nikhil.kt_notes.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoogleSignIn: ImageButton
    private lateinit var elEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var elPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var txtLoginScreen: TextView

    override fun onStart() {
        super.onStart()
        auth.signOut()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        initialization()

        auth = FirebaseAuth.getInstance()
        btnLogin.setOnClickListener { signupUser() }

        txtLoginScreen.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun signupUser() {
        val email = etEmail.text?.trim().toString()
        val password = etPassword.text?.trim().toString()

        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(email, password).await()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SignUpActivity,
                                "User created Successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Intent(applicationContext, NotesActivity::class.java).also {
                                startActivity(it)
                            }
                            finish()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                elPassword.error = "Please enter a valid password!!"
            }
        } else {
            elEmail.error = "Please enter a valid Email!!"
        }
    }

    private fun initialization() {
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        elEmail = findViewById(R.id.elEmail)
        elPassword = findViewById(R.id.elPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        txtLoginScreen = findViewById(R.id.txtLoginScreen)
    }
}