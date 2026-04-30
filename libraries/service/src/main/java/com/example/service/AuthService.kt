package com.example.service

import android.util.Patterns
import com.example.service.repository.AuthInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthService @Inject constructor(
    private val firebaseService: FirebaseService
) : AuthInterface {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun login(
        email: String,
        password: String,
        deviceId: String,
        onResult: (Result<Unit>) -> Unit
    ) {

        auth.signOut()

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    onResult(Result.failure(task.exception ?: Exception("Login failed")))
                    return@addOnCompleteListener
                }

                val user = auth.currentUser

                user?.reload()?.addOnCompleteListener {

                    if (user == null || !user.isEmailVerified) {
                        auth.signOut()
                        onResult(Result.failure(Exception("Please verify your email first")))
                        return@addOnCompleteListener
                    }

                    val uid = user.uid

                    db.collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { doc ->

                            if (!doc.exists()) {
                                auth.signOut()
                                onResult(Result.failure(Exception("User data not found")))
                                return@addOnSuccessListener
                            }

                            val savedDeviceId = doc.getString("deviceId")

                            if (deviceId == savedDeviceId) {
                                onResult(Result.success(Unit))
                            } else {
                                auth.signOut()
                                onResult(
                                    Result.failure(
                                        Exception("Account already used on another device")
                                    )
                                )
                            }
                        }
                        .addOnFailureListener {
                            onResult(Result.failure(Exception("Database error")))
                        }
                }
            }
    }

    override fun sendPasswordReset(
        email: String,
        onResult: (Result<String>) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(Result.success("Reset link sent to your email"))
                } else {
                    onResult(
                        Result.failure(
                            task.exception ?: Exception("Error")
                        )
                    )
                }
            }
    }

    override fun resendVerification(
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
                        onResult(
                            Result.failure(
                                task.exception ?: Exception("Failed ❌")
                            )
                        )
                    }
                }
        }
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun validateLogin(email: String, password: String): String {
        return when {
            email.isBlank() && password.isBlank() -> "Fill all fields"
            email.isBlank() -> "Email required"
            !isValidEmail(email) -> "Invalid email format"
            password.isBlank() -> "Password required"
            else -> ""
        }
    }

    override fun validateSignup(
        username: String,
        email: String,
        phone: String,
        password: String
    ): String {

        if (username.isBlank()) return "Enter username"
        if (email.isBlank()) return "Enter email"
        if (phone.isBlank()) return "Enter mobile number"
        if(!isValidEmail(email)) return "Invalid email format"
        val passwordError = validatePassword(password)
        if (passwordError.isNotEmpty()) return passwordError
        return ""
    }
    fun validatePassword(password: String): String {

        if (password.length < 8)
            return "Password must be at least 8 characters"

        if (!password.any { it.isUpperCase() })
            return "Password must contain 1 uppercase letter"

        if (!password.any { it.isLowerCase() })
            return "Password must contain 1 lowercase letter"

        if (!password.any { it.isDigit() })
            return "Password must contain 1 number"

        if (!password.any { !it.isLetterOrDigit() })
            return "Password must contain 1 special character"

        return ""
    }

}
