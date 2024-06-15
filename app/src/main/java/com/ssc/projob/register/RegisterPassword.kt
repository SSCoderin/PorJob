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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.ssc.projob.MainActivity
import com.ssc.projob.R

class RegisterPassword : AppCompatActivity() {

    private lateinit var btnCandidatePassword: Button
    private lateinit var btnEmployeePassword: Button
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var btnSetPassword: Button
    private lateinit var textViewLogin: TextView


    private lateinit var auth: FirebaseAuth
    private var selectedRole: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_password)

        btnCandidatePassword = findViewById(R.id.btnCandidatePassword)
        btnEmployeePassword = findViewById(R.id.btnEmployeePassword)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        btnSetPassword = findViewById(R.id.btnSetPassword)
        textViewLogin = findViewById(R.id.textViewLogin)


        auth = Firebase.auth

        selectedRole = intent.getStringExtra("role").toString()
        updateRoleButtons()

        btnCandidatePassword.setOnClickListener { onCandidateSelected() }
        btnEmployeePassword.setOnClickListener { onEmployeeSelected() }
        btnSetPassword.setOnClickListener { setPassword() }
        textViewLogin.setOnClickListener { navigateToLogin() }

    }

    private fun updateRoleButtons() {
        if (selectedRole == "Candidate") {
            btnCandidatePassword.alpha = 1.0f
            btnEmployeePassword.alpha = 0.5f
        } else if (selectedRole == "Employee") {
            btnEmployeePassword.alpha = 1.0f
            btnCandidatePassword.alpha = 0.5f
        }
    }

    private fun onCandidateSelected() {
        selectedRole = "Candidate"
        updateRoleButtons()
    }

    private fun onEmployeeSelected() {
        selectedRole = "Employee"
        updateRoleButtons()
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
            // Here you would typically save the password securely and complete the registration process
            saveUserData(user, password)
        }
    }

    private fun saveUserData(user: FirebaseUser, password: String) {
        val db = Firebase.firestore
        val userData = hashMapOf(
            "uid" to user.uid,
            "phone" to user.phoneNumber,
            "role" to selectedRole,
            "password" to password // Note: Store passwords securely in real applications
        )

        db.collection("users")
            .document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to register user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }
}
