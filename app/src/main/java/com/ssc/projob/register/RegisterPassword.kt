package com.ssc.projob.register

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ssc.projob.MainActivity
import com.ssc.projob.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.reactivestreams.KMongo

data class User(
    val uid: String,
    val phoneNumber: String,
    val role: String,
    val password: String
)

class RegisterPassword : AppCompatActivity() {

    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var btnSetPassword: Button
    private lateinit var textViewLogin: TextView

    private lateinit var auth: FirebaseAuth
    private val selectedRole: String = "Candidate" // Default role is set to Candidate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_password)

        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        btnSetPassword = findViewById(R.id.btnSetPassword)
        textViewLogin = findViewById(R.id.textViewLogin)

        auth = Firebase.auth

        btnSetPassword.setOnClickListener { setPassword() }
        textViewLogin.setOnClickListener { navigateToLogin() }
    }

    private fun setPassword() {
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Enter both password and confirm password", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user != null) {
            // Save user data to MongoDB directly
            saveUserData(user.uid, user.phoneNumber, password)
        }
    }

    private fun saveUserData(uid: String, phoneNumber: String?, password: String) {
        val client = KMongo.createClient("mongodb+srv://shivkiran:Spider123@cluster0.dweb88l.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0").coroutine
        val database = client.getDatabase("Pro_Job")
        val collection = database.getCollection<User>()

        val user = User(uid, phoneNumber ?: "", selectedRole, password)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                collection.insertOne(user)
                runOnUiThread {
                    Toast.makeText(this@RegisterPassword, "User registered successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterPassword, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@RegisterPassword, "Failed to register user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }
}
