package com.ssc.projob.register


import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.ssc.projob.MainActivity
import com.ssc.projob.R
import java.util.concurrent.TimeUnit

class RegisterOtp : AppCompatActivity() {

    private lateinit var btnCandidate: Button
    private lateinit var editTextPhone: EditText
    private lateinit var editTextOtp: EditText
    private lateinit var btnGetOtp: Button
    private lateinit var btnVerifyOtp: Button
    private lateinit var textViewLogin: TextView

    private lateinit var auth: FirebaseAuth
    private var selectedRole: String = ""

    // SMS Retriever variables
    private lateinit var smsVerificationReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_otp)

        btnCandidate = findViewById(R.id.btnCandidate)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextOtp = findViewById(R.id.editTextOtp)
        btnGetOtp = findViewById(R.id.btnGetOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
        textViewLogin = findViewById(R.id.textViewLogin)

        auth = FirebaseAuth.getInstance()

        btnCandidate.setOnClickListener { onCandidateSelected() }
        btnGetOtp.setOnClickListener { getOtp() }
        btnVerifyOtp.setOnClickListener { verifyOtp() }
        textViewLogin.setOnClickListener { navigateToLogin() }

        // Start SMS Retriever when activity starts
        startSmsRetriever()

        // Register BroadcastReceiver to listen for SMS Retriever events
        registerSmsVerificationReceiver()
    }

    private fun startSmsRetriever() {
        val client: SmsRetrieverClient = SmsRetriever.getClient(this /* context */)

        // Starts SmsRetriever, waits for ONE matching SMS message until timeout
        val task = client.startSmsRetriever()

        task.addOnSuccessListener(object : OnSuccessListener<Void?> {
            override fun onSuccess(aVoid: Void?) {
                // SMS Retriever has been initiated successfully
                Log.d(TAG, "SMS Retriever has started.")
            }
        })

        task.addOnFailureListener(object : OnFailureListener {
            override fun onFailure(e: Exception) {
                // Failed to start SMS Retriever
                Log.e(TAG, "Failed to start SMS Retriever: $e")
            }
        })
    }

    private fun registerSmsVerificationReceiver() {
        smsVerificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                    val extras = intent.extras
                    val smsMessage = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    // Extract OTP from SMS message
                    extractOtpFromMessage(smsMessage)
                }
            }
        }

        // Register the receiver
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)
    }

    private fun extractOtpFromMessage(message: String) {
        // Extract OTP code from message here, e.g., using regex or substring
        // Example: "Your OTP code is: 123456"
        val otpRegex = "(\\d{6})".toRegex()
        val matchResult = otpRegex.find(message)
        val otp = matchResult?.value

        // Auto-fill OTP in EditText
        editTextOtp.setText(otp)

        // Now you can proceed with OTP verification using Firebase Authentication
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp!!)
        signInWithPhoneAuthCredential(credential)
    }

    private fun onCandidateSelected() {
        selectedRole = "Candidate"
        btnCandidate.alpha = 1.0f

    }

    private fun onEmployeeSelected() {
        selectedRole = "Employee"
        btnCandidate.alpha = 0.5f
        btnCandidate.isEnabled = false
    }

    private fun getOtp() {
        val phone = editTextPhone.text.toString().trim()
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
            return
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone, 60, TimeUnit.SECONDS, this, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically verify and sign in the user
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@RegisterOtp, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Save verification ID and resending token so we can use them later
                    storedVerificationId = verificationId
                    resendToken = token
                }
            }
        )
    }

    private fun verifyOtp() {
        val otp = editTextOtp.text.toString().trim()
        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, navigate to the password page
                    val intent = Intent(this, RegisterPassword::class.java)
                    intent.putExtra("role", selectedRole)
                    intent.putExtra("phoneNumber", editTextPhone.text.toString().trim())
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(smsVerificationReceiver)
    }

    companion object {
        private const val TAG = "RegisterOtp"
        private lateinit var storedVerificationId: String
        private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    }
}
//package com.ssc.projob.register
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Bundle
//import android.text.TextUtils
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.auth.api.phone.SmsRetriever
//import com.google.android.gms.auth.api.phone.SmsRetrieverClient
//import com.google.android.gms.tasks.OnFailureListener
//import com.google.android.gms.tasks.OnSuccessListener
//import com.google.firebase.FirebaseException
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.PhoneAuthCredential
//import com.google.firebase.auth.PhoneAuthProvider
//import com.ssc.projob.MainActivity
//import com.ssc.projob.R
//import java.util.concurrent.TimeUnit
//
//class RegisterOtp : AppCompatActivity() {
//
//    private lateinit var editTextPhone: EditText
//    private lateinit var editTextOtp: EditText
//    private lateinit var btnGetOtp: Button
//    private lateinit var btnVerifyOtp: Button
//    private lateinit var textViewLogin: TextView
//
//    private lateinit var auth: FirebaseAuth
//
//    // SMS Retriever variables
//    private lateinit var smsVerificationReceiver: BroadcastReceiver
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register_otp)
//
//        editTextPhone = findViewById(R.id.editTextPhone)
//        editTextOtp = findViewById(R.id.editTextOtp)
//        btnGetOtp = findViewById(R.id.btnGetOtp)
//        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
//        textViewLogin = findViewById(R.id.textViewLogin)
//
//        auth = FirebaseAuth.getInstance()
//
//        btnGetOtp.setOnClickListener { getOtp() }
//        btnVerifyOtp.setOnClickListener { verifyOtp() }
//        textViewLogin.setOnClickListener { navigateToLogin() }
//
//        // Start SMS Retriever when activity starts
//        startSmsRetriever()
//
//        // Register BroadcastReceiver to listen for SMS Retriever events
//        registerSmsVerificationReceiver()
//    }
//
//    private fun startSmsRetriever() {
//        val client: SmsRetrieverClient = SmsRetriever.getClient(this /* context */)
//
//        // Starts SmsRetriever, waits for ONE matching SMS message until timeout
//        val task = client.startSmsRetriever()
//
//        task.addOnSuccessListener(object : OnSuccessListener<Void?> {
//            override fun onSuccess(aVoid: Void?) {
//                // SMS Retriever has been initiated successfully
//                Log.d(TAG, "SMS Retriever has started.")
//            }
//        })
//
//        task.addOnFailureListener(object : OnFailureListener {
//            override fun onFailure(e: Exception) {
//                // Failed to start SMS Retriever
//                Log.e(TAG, "Failed to start SMS Retriever: $e")
//            }
//        })
//    }
//
//    private fun registerSmsVerificationReceiver() {
//        smsVerificationReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
//                    val extras = intent.extras
//                    val smsMessage = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
//
//                    // Extract OTP from SMS message
//                    extractOtpFromMessage(smsMessage)
//                }
//            }
//        }
//
//        // Register the receiver
//        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
//        registerReceiver(smsVerificationReceiver, intentFilter)
//    }
//
//    private fun extractOtpFromMessage(message: String) {
//        // Extract OTP code from message here, e.g., using regex or substring
//        // Example: "Your OTP code is: 123456"
//        val otpRegex = "(\\d{6})".toRegex()
//        val matchResult = otpRegex.find(message)
//        val otp = matchResult?.value
//
//        // Auto-fill OTP in EditText
//        editTextOtp.setText(otp)
//
//        // Now you can proceed with OTP verification using Firebase Authentication
//        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp!!)
//        signInWithPhoneAuthCredential(credential)
//    }
//
//    private fun getOtp() {
//        val phone = editTextPhone.text.toString().trim()
//        if (TextUtils.isEmpty(phone)) {
//            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//            phone, 60, TimeUnit.SECONDS, this, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                    // Automatically verify and sign in the user
//                    signInWithPhoneAuthCredential(credential)
//                }
//
//                override fun onVerificationFailed(e: FirebaseException) {
//                    Toast.makeText(this@RegisterOtp, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
//                    // Save verification ID and resending token so we can use them later
//                    storedVerificationId = verificationId
//                    resendToken = token
//                }
//            }
//        )
//    }
//
//    private fun verifyOtp() {
//        val otp = editTextOtp.text.toString().trim()
//        if (TextUtils.isEmpty(otp)) {
//            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
//        signInWithPhoneAuthCredential(credential)
//    }
//
//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, navigate to the password page
//                    val intent = Intent(this, RegisterPassword::class.java)
//                    intent.putExtra("role", "Candidate") // Default role is set to Candidate
//                    intent.putExtra("phoneNumber", editTextPhone.text.toString().trim())
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private fun navigateToLogin() {
//        val intent = Intent(this, LogIn::class.java)
//        startActivity(intent)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Unregister the receiver when the activity is destroyed
//        unregisterReceiver(smsVerificationReceiver)
//    }
//
//    companion object {
//        private const val TAG = "RegisterOtp"
//        private lateinit var storedVerificationId: String
//        private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
//    }
//}
