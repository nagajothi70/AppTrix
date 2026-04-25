package com.example.apptrix

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.example.apptrix.ui.HomeScreen
import com.example.apptrix.ui.authentication.AuthLoadingScreen
import com.example.apptrix.ui.authentication.BiometricScreen
import com.example.apptrix.ui.authentication.EmailVerificationScreen
import com.example.apptrix.ui.authentication.ForgotPasswordScreen
import com.example.apptrix.ui.authentication.LoginScreen
import com.example.apptrix.ui.authentication.OtpScreen
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

    val startDestination = when {
        savedTime == 0L || days >= 7 -> Screen.Login.route
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
            AuthLoadingScreen()
        }

        composable("otp/{phone}") { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            OtpScreen(navController, phone)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }

        composable("${Screen.EmailVerify.route}/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(navController, email)
        }
    }
}

