package com.example.apptrix

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apptrix.ui.authentication.LoginScreen
import com.example.apptrix.ui.authentication.SignupScreen
import com.example.models.sealed.Screen

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Signup.route) {
            SignupScreen(navController)
        }

        composable(Screen.Home.route) {
            Text("Home Screen 🚀")
        }
    }
}