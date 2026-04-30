package com.example.service

import android.app.Activity
import com.example.models.sealed.OtpResult
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseService @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 🔥 CREATE USER
    fun createUser(
        email: String,
        password: String,
        onResult: (Result<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                val uid = auth.currentUser?.uid
                if (it.isSuccessful && uid != null) {
                    onResult(Result.success(uid))
                } else {
                    onResult(Result.failure(it.exception ?: Exception("Signup failed")))
                }
            }
    }

    // 🔥 SAVE USER DATA
    fun saveUser(
        uid: String,
        data: Map<String, Any>,
        onResult: (Result<Unit>) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .set(data)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    // 🔥 EMAIL VERIFICATION
    fun sendEmailVerification(
        onResult: (Result<Unit>) -> Unit
    ) {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener {
                if (it.isSuccessful) onResult(Result.success(Unit))
                else onResult(Result.failure(it.exception ?: Exception("Verification failed")))
            }
    }

    // 🔥 LOGIN
    fun login(
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                if (it.isSuccessful) onResult(Result.success(Unit))
                else onResult(Result.failure(it.exception ?: Exception("Login failed")))
            }
    }

    // 🔥 GET USER
    fun getUser(
        uid: String,
        onResult: (Result<Map<String, Any>?>) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { onResult(Result.success(it.data)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun getCurrentUserUid(): String? = auth.currentUser?.uid
    fun isEmailVerified(): Boolean = auth.currentUser?.isEmailVerified == true
    fun signOut() = auth.signOut()

    // 🔥 PASSWORD RESET
    fun sendPasswordReset(
        email: String,
        onResult: (Result<String>) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) onResult(Result.success("Reset link sent"))
                else onResult(Result.failure(it.exception ?: Exception("Error")))
            }
    }

    // 🔥 RESEND EMAIL VERIFICATION
    fun resendEmailVerification(
        onResult: (Result<String>) -> Unit
    ) {
        val user = auth.currentUser

        if (user == null) {
            onResult(Result.failure(Exception("Session expired. Please signup again.")))
            return
        }

        user.reload().addOnCompleteListener {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(Result.success("Verification email sent ✔"))
                    } else {
                        onResult(Result.failure(task.exception ?: Exception("Failed ❌")))
                    }
                }
        }
    }

    // 🔥 SEND OTP
    fun sendOtp(
        phone: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken?,
        isResend: Boolean,
        onResult: (OtpResult) -> Unit
    ) {
        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

                override fun onVerificationFailed(e: FirebaseException) {
                    onResult(OtpResult.Error(e.message ?: "OTP failed"))
                }

                override fun onCodeSent(
                    verId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    onResult(OtpResult.CodeSent(verId, token))
                }
            })

        if (isResend && resendToken != null) {
            builder.setForceResendingToken(resendToken)
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    // 🔥 VERIFY OTP
    fun verifyOtp(
        verificationId: String,
        otp: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)

        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) onResult(Result.success(Unit))
                else onResult(Result.failure(Exception("Invalid OTP")))
            }
    }
}
