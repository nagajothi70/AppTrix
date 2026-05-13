package com.example.models.ui

data class EmailVerificationState(
    val message: String = "",
    val isSending: Boolean = false,
    val canResend: Boolean = false,
    val timer: Int = 90
)
