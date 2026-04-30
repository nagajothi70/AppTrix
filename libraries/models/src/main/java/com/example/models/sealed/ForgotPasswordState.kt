package com.example.models.sealed

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSuccess: Boolean = false
)
