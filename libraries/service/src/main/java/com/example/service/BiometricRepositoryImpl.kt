package com.example.service

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.models.sealed.BiometricResult
import com.example.service.repository.BiometricRepository

class BiometricRepositoryImpl : BiometricRepository {

    override fun authenticate(
        activity: FragmentActivity,
        onResult: (BiometricResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,

            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    onResult(BiometricResult.Success)
                }

                override fun onAuthenticationFailed() {
                    onResult(BiometricResult.Failed)
                }

                override fun onAuthenticationError(code: Int, err: CharSequence) {
                    onResult(BiometricResult.Error(err.toString()))
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("AppTrix Login")
            .setSubtitle("Use fingerprint to continue")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
