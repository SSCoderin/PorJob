package com.ssc.projob.register

import com.ssc.projob.MainActivity


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ssc.projob.R
import com.ssc.projob.dashboard

class LogIn : AppCompatActivity() {

    private lateinit var editTextPhoneLogin: EditText
    private lateinit var editTextPasswordLogin: EditText
    private lateinit var btnLogin: Button
    private lateinit var textViewRegister: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        editTextPhoneLogin = findViewById(R.id.editTextPhoneLogin)
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin)
        btnLogin = findViewById(R.id.btnLogin)
        textViewRegister=findViewById(R.id.textRegister)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener { login() }
        textViewRegister.setOnClickListener { navigateToRegister() }

    }

    private fun login() {
        val phone = editTextPhoneLogin.text.toString().trim()
        val password = editTextPasswordLogin.text.toString().trim()

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter phone number and password", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user != null) {
            fetchUserData(user.uid, password)
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserData(uid: String, inputPassword: String) {
        val db = Firebase.firestore

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val storedPassword = document.getString("password")
                    if (storedPassword == inputPassword) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, dashboard::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterOtp::class.java)
        startActivity(intent)
    }
}


