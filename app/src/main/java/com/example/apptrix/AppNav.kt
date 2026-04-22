package com.example.apptrix

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.apptrix.ui.HomeScreen
import com.example.apptrix.ui.authentication.AuthLoadingScreen
import com.example.apptrix.ui.authentication.AuthLoadingView
import com.example.apptrix.ui.authentication.BiometricScreen
import com.example.apptrix.ui.authentication.LoginScreen
import com.example.apptrix.ui.authentication.SignupScreen
import com.example.models.sealed.Screen
import com.example.security.SessionManager

@Composable
fun AppNav() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val sessionManager = SessionManager(context)

    val savedTime = sessionManager.getLoginTime()
    val currentTime = System.currentTimeMillis()

    val diff = currentTime - savedTime
    val days = diff / (1000 * 60 * 60 * 24)

    // 🔥 Dynamic start destination
    val startDestination = when {
        savedTime == 0L || days >= 30 -> Screen.Login.route
        else -> Screen.Biometric.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Signup.route) {
            SignupScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.Biometric.route) {
            BiometricScreen(navController)
        }

        composable(Screen.AuthLoading.route) {
            AuthLoadingScreen(navController)
        }
    }
}

