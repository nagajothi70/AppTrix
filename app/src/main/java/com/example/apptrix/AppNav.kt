package com.example.apptrix

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.apptrix.ui.HomeScreen
import com.example.apptrix.ui.authentication.AuthLoadingScreen
import com.example.apptrix.ui.authentication.BiometricScreen
import com.example.apptrix.ui.authentication.EmailVerificationScreen
import com.example.apptrix.ui.authentication.ForgotPasswordScreen
import com.example.apptrix.ui.authentication.LoginScreen
import com.example.apptrix.ui.authentication.OtpScreen
import com.example.apptrix.ui.authentication.SignupScreen
import com.example.models.ui.Screen

@Composable
fun AppNav(startDestination: String, email: String) {

    val navController = rememberNavController()

    val start = when (startDestination) {
        "login" -> Screen.Login.route
        "email_verification" -> "${Screen.EmailVerify.route}/$email"
        "biometric" -> Screen.Biometric.route
        else -> Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = start
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
