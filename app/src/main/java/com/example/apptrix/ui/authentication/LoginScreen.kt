package com.example.apptrix.ui.authentication

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import com.example.service.DeviceService
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {

    val viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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

            // 🔥 NEW: Forgot Password
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "Forgot Password?",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.ForgotPassword.route)
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            ErrorText(errorMessage)

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {

                    errorMessage = ""

                    val validation = viewModel.validateLogin(email, password)

                    if (validation.isNotEmpty()) {
                        errorMessage = validation
                        return@Button
                    }



                    isLoading = true

                    Log.d("LOGIN_DEBUG", "Login button clicked")
                    Log.d("LOGIN_DEBUG", "Email: $email")

                    // 🔥 clear old session
                    auth.signOut()
                    Log.d("LOGIN_DEBUG", "Signed out previous session")

                    auth.signInWithEmailAndPassword(email.trim(), password.trim())
                        .addOnCompleteListener { task ->

                            Log.d("LOGIN_DEBUG", "Task success: ${task.isSuccessful}")
                            Log.d("LOGIN_DEBUG", "Exception: ${task.exception?.message}")

                            if (task.isSuccessful) {

                                val user = auth.currentUser

                                user?.reload()?.addOnCompleteListener {

                                    if (user == null || !user.isEmailVerified){
                                        auth.signOut()
                                        isLoading = false
                                        errorMessage = "Please verify your email first"
                                        return@addOnCompleteListener
                                    }

                                    val uid = auth.currentUser?.uid
                                    Log.d("LOGIN_DEBUG", "UID: $uid")

                                    if (uid == null) {
                                        isLoading = false
                                        errorMessage = "User error"
                                        return@addOnCompleteListener
                                    }

                                    val currentDeviceId = DeviceService.getDeviceId(context)
                                    Log.d("LOGIN_DEBUG", "Current Device ID: $currentDeviceId")

                                    FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener { document ->

                                            Log.d("LOGIN_DEBUG", "Firestore success")

                                            if (!document.exists()) {
                                                isLoading = false
                                                errorMessage = "User data not found"
                                                auth.signOut()
                                                Log.d("LOGIN_DEBUG", "User document not found")
                                                return@addOnSuccessListener
                                            }

                                            val savedDeviceId = document.getString("deviceId")
                                            Log.d("LOGIN_DEBUG", "Saved Device ID: $savedDeviceId")

                                            // 🔥 CHANGE ONLY THIS BLOCK INSIDE SUCCESS

                                            if (currentDeviceId == savedDeviceId) {

                                                Log.d(
                                                    "LOGIN_DEBUG",
                                                    "Device matched → LOGIN SUCCESS"
                                                )

                                                val sessionManager = SessionManager(context)
                                                sessionManager.saveLoginTime()

                                                isLoading = false

                                                // ✅ DIRECT HOME NAVIGATION (FIX)
                                                navController.navigate(Screen.Home.route) {
                                                    popUpTo(Screen.Login.route) { inclusive = true }
                                                }

                                            } else {

                                                Log.d(
                                                    "LOGIN_DEBUG",
                                                    "Device mismatch → LOGIN BLOCKED"
                                                )

                                                isLoading = false
                                                auth.signOut()
                                                errorMessage =
                                                    "Account already used on another device"
                                            }
                                        }
                                        .addOnFailureListener {

                                            Log.d("LOGIN_DEBUG", "Firestore error: ${it.message}")

                                            isLoading = false
                                            errorMessage = "Database error"
                                        }
                                }

                            } else {

                                Log.d("LOGIN_DEBUG", "LOGIN FAILED")

                                isLoading = false
                                auth.signOut() // 🔥 force clear
                                errorMessage =
                                    task.exception?.message ?: "Invalid email or password"
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