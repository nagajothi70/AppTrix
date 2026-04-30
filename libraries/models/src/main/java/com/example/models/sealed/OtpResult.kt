package com.example.models.sealed

import com.google.firebase.auth.PhoneAuthProvider

sealed class OtpResult {
    data class CodeSent(
        val verificationId: String,
        val token: PhoneAuthProvider.ForceResendingToken
    ) : OtpResult()

    data class Error(val message: String) : OtpResult()
}
