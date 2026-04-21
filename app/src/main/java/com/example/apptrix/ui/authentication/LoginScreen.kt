package com.example.apptrix.ui.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.models.sealed.Screen
import com.example.security.SessionManager
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D2A5C), Color(0xFF123E7C))
                )
            )
            .padding(24.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                "Login to your Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(40.dp))

            AppTextField(email, { email = it }, "Email", KeyboardType.Email)

            Spacer(Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },

                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else PasswordVisualTransformation(),

                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },

                colors = appTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red)
            }

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {

                    errorMessage = ""

                    if (email.isBlank() && password.isBlank()) {
                        errorMessage = "Fill all fields"
                        return@Button
                    }

                    if (email.isBlank()) {
                        errorMessage = "Email required"
                        return@Button
                    }

                    if (password.isBlank()) {
                        errorMessage = "Password required"
                        return@Button
                    }

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val context = navController.context
                                val sessionManager = SessionManager(context)

                                sessionManager.saveLoginTime() // ⭐ VERY IMPORTANT

                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }

                                }
                            } else {
                                errorMessage = "Email or password incorrect"
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4A90E2))
            ) {
                Text("Login", color = Color.White)
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account? ", color = Color.LightGray)

                Text(
                    "Sign Up",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Signup.route)
                    }
                )
            }
        }
    }
}