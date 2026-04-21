package com.example.apptrix.ui.authentication

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.models.sealed.Screen
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun BiometricScreen(navController: NavController) {

    val context = LocalContext.current
    val activity = context as? androidx.fragment.app.FragmentActivity

    if (activity == null) {
        Text("Biometric not supported")
        return
    }

    LaunchedEffect(Unit) {

        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(
            activity, // ⭐ MUST BE FragmentActivity
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Biometric.route) { inclusive = true }
                    }
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("AppTrix Login")
            .setSubtitle("Use fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo) // ⭐ error line fixed
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Authenticating...")
    }
}