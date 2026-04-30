package com.example.apptrix.ui.authentication

import androidx.lifecycle.ViewModel
import com.example.service.repository.AuthInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthInterface
) : ViewModel() {

    fun validateLogin(email: String, password: String): String {
      return repo.validateLogin(email = email , password = password)
    }

    fun validateSignup(
        username: String,
        email: String,
        phone: String,
        password: String
    ): String {
        return repo.validateSignup(username = username, email = email, phone = phone, password = password)
    }
}
