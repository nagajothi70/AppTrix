package com.example.service

import android.app.Activity
import android.util.Patterns
import com.example.models.sealed.OtpResult
import com.example.service.repository.AuthInterface
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject

class AuthService @Inject constructor(
    private val firebaseService: FirebaseService
) : AuthInterface {

    // 🔥 OTP
    override fun sendOtp(
        phone: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken?,
        isResend: Boolean,
        onResult: (OtpResult) -> Unit
    ) {
        firebaseService.sendOtp(phone, activity, resendToken, isResend, onResult)
    }

    override fun verifyOtp(
        verificationId: String,
        otp: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        firebaseService.verifyOtp(verificationId, otp, onResult)
    }

    override fun resendVerification(
        onResult: (Result<String>) -> Unit
    ) {
        firebaseService.resendEmailVerification(onResult)
    }

    // 🔥 LOGIN
    override fun login(
        email: String,
        password: String,
        deviceId: String,
        onResult: (Result<Unit>) -> Unit
    ) {

        firebaseService.signOut()

        firebaseService.login(email, password) { result ->

            result.onFailure {
                onResult(Result.failure(it))
                return@login
            }

            val uid = firebaseService.getCurrentUserUid()
            val isVerified = firebaseService.isEmailVerified()

            if (uid == null || !isVerified) {
                firebaseService.signOut()
                onResult(Result.failure(Exception("Please verify your email first")))
                return@login
            }

            firebaseService.getUser(uid) { userResult ->

                userResult.onSuccess { data ->

                    val savedDeviceId = data?.get("deviceId") as? String

                    if (savedDeviceId == deviceId) {
                        onResult(Result.success(Unit))
                    } else {
                        firebaseService.signOut()
                        onResult(Result.failure(Exception("Account already used on another device")))
                    }

                }.onFailure {
                    onResult(Result.failure(Exception("Database error")))
                }
            }
        }
    }

    // 🔥 SIGNUP
    override fun signup(
        username: String,
        email: String,
        phone: String,
        password: String,
        deviceId: String,
        onResult: (Result<Unit>) -> Unit
    ) {

        firebaseService.createUser(email, password) { result ->

            result.onSuccess { uid ->

                val userMap = mapOf(
                    "username" to username,
                    "email" to email,
                    "phone" to phone,
                    "deviceId" to deviceId
                )

                firebaseService.saveUser(uid, userMap) {

                    it.onSuccess {

                        firebaseService.sendEmailVerification { verifyResult ->
                            verifyResult.onSuccess {
                                onResult(Result.success(Unit))
                            }.onFailure {
                                onResult(Result.failure(it))
                            }
                        }

                    }.onFailure {
                        onResult(Result.failure(it))
                    }
                }

            }.onFailure {
                onResult(Result.failure(it))
            }
        }
    }

    // 🔥 RESET
    override fun sendPasswordReset(
        email: String,
        onResult: (Result<String>) -> Unit
    ) {
        firebaseService.sendPasswordReset(email, onResult)
    }

    // 🔥 VALIDATION
    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

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
        if (!isValidEmail(email)) return "Invalid email format"

        val passwordError = validatePassword(password)
        if (passwordError.isNotEmpty()) return passwordError

        return ""
    }

    private fun validatePassword(password: String): String {

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
