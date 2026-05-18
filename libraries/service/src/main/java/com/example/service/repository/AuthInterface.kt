package com.example.service.repository

import android.content.Context

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

    fun sendPasswordReset(
        email: String,
        onResult: (Result<String>) -> Unit
    )

    fun login(
        email: String,
        password: String,
        deviceId: String,
        onResult: (Result<Unit>) -> Unit
    )

    fun signup(
        context: Context,
        username: String,
        email: String,
        phone: String,
        password: String,
        deviceId: String,
        onResult: (Result<Unit>) -> Unit
    )
}
