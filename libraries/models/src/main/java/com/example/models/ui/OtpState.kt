package com.example.models.ui

import com.google.firebase.auth.PhoneAuthProvider

data class OtpState(
    val otp: String = "",
    val verificationId: String? = null,
    val resendToken: PhoneAuthProvider.ForceResendingToken? = null,
    val isLoading: Boolean = false,
    val message: String = "",
    val timer: Int = 60,
    val canResend: Boolean = false,
    val isVerified: Boolean = false
)
