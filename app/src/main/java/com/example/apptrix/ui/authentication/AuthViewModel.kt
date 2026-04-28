package com.example.apptrix.ui.authentication

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.service.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateLogin(email: String, password: String): String {
        return when {
            email.isBlank() && password.isBlank() -> "Fill all fields"
            email.isBlank() -> "Email required"
            !isValidEmail(email) -> "Invalid email format"
            password.isBlank() -> "Password required"
            else -> ""
        }
    }

    fun validateSignup(
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
