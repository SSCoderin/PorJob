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
import com.ssc.projob.dashboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            saveUserData(user.uid, user.phoneNumber, password)
        }
    }

    private fun saveUserData(uid: String, phoneNumber: String?, password: String) {
        val retrofit = RetrofitClient.getClient("http://localhost:6500")
        val apiService = retrofit.create(ApiService::class.java)

        val user = User(uid, phoneNumber ?: "", selectedRole, password)
        val call = apiService.registerUser(user)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterPassword, "User registered successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterPassword, dashboard::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@RegisterPassword, "Failed to register user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterPassword, "Failed to register user: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }
}
