package com.example.service.repository

interface AuthInterface {

    fun validateLogin(email: String, password: String): String

    fun validateSignup(
        username: String,
        email: String,
        phone: String,
        password: String
    ): String

    fun resendVerification(
        onResult: (Result<String>) -> Unit
    )
}
