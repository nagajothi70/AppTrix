package com.example.models.ui

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSuccess: Boolean = false
)
