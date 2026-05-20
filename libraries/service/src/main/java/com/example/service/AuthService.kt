package com.example.service

import android.content.Context
import com.example.service.repository.AuthInterface
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.security.SessionManager


class AuthService @Inject constructor(
    private val firebaseService: FirebaseService
) : AuthInterface {

    private val db = FirebaseFirestore.getInstance()

    override fun resendVerification(
        onResult: (Result<String>) -> Unit
    ) {
        firebaseService.resendEmailVerification(onResult)
    }

    // 🔥 LOGIN
    override fun login(
        context: Context,
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

                onResult(
                    Result.failure(
                        Exception("Please verify your email first")
                    )
                )

                return@login
            }

            firebaseService.getUser(uid) { userResult ->

                userResult.onSuccess { data ->

                    val savedDeviceId =
                        data?.get("deviceId") as? String

                    if (savedDeviceId == deviceId) {

                        val username = data?.get("username") as? String ?: ""
                        val email = data?.get("email") as? String ?: ""
                        val role = data?.get("role") as? String ?: ""

                        CoroutineScope(Dispatchers.IO).launch {

                            SessionManager(context).saveSession(
                                uid = uid,
                                username = username,
                                email = email,
                                role = role,
                                deviceId = deviceId
                            )

                            onResult(Result.success(Unit))
                        }

                    } else {

                        firebaseService.signOut()

                        onResult(
                            Result.failure(
                                Exception(
                                    "Account already used on another device"
                                )
                            )
                        )
                    }

                }.onFailure {

                    onResult(
                        Result.failure(
                            Exception("Database error")
                        )
                    )
                }
            }
        }
    }

    // 🔥 SIGNUP
    override fun signup(
        context: Context,
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
                    "deviceId" to deviceId,
                    "role" to "student"

                )

                firebaseService.saveUser(uid, userMap) {

                    it.onSuccess {

                        firebaseService.saveUserToBackend(
                            context = context,
                            firebaseUID = uid,
                            name = username,
                            email = email,
                            phone = phone,
                            role = "student",
                            deviceId = deviceId
                        )

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

    // 🔥 RESET PASSWORD
    override fun sendPasswordReset(
        email: String,
        onResult: (Result<String>) -> Unit
    ) {

        if (email.isBlank()) {

            onResult(
                Result.failure(
                    Exception("Enter email")
                )
            )

            return
        }

        db.collection("users")
            .whereEqualTo("email", email.trim())
            .whereEqualTo("role", "student")
            .get()

            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {

                    onResult(
                        Result.failure(
                            Exception(
                                "Student account not found"
                            )
                        )
                    )

                    return@addOnSuccessListener
                }

                firebaseService.sendPasswordReset(
                    email.trim(),
                    onResult
                )
            }

            .addOnFailureListener {

                onResult(
                    Result.failure(
                        Exception("Database error")
                    )
                )
            }
    }

    // 🔥 VALIDATION
    private fun isValidEmail(email: String): Boolean {

        val emailRegex =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"

        return email.matches(emailRegex.toRegex())
    }

    override fun validateLogin(
        email: String,
        password: String
    ): String {

        return when {

            email.isBlank() && password.isBlank() ->
                "Fill all fields"

            email.isBlank() ->
                "Email required"

            !isValidEmail(email) ->
                "Invalid email format"

            password.isBlank() ->
                "Password required"

            else -> ""
        }
    }

    override fun validateSignup(
        username: String,
        email: String,
        phone: String,
        password: String
    ): String {

        if (username.isBlank())
            return "Enter username"

        if (email.isBlank())
            return "Enter email"

        if (!isValidEmail(email))
            return "Invalid email format"

        if (phone.isBlank())
            return "Enter mobile number"

        val passwordError =
            validatePassword(password)

        if (passwordError.isNotEmpty())
            return passwordError

        return ""
    }

    private fun validatePassword(
        password: String
    ): String {

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
