package com.example.models.sealed

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object Biometric : Screen("biometric")
    object AuthLoading : Screen("auth_loading")
    object Otp : Screen("otp")
}