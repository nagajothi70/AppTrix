package com.example.apptrix.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
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
                "Reset Password",
                fontSize = 28.sp,
                color = Color.White
            )

            Spacer(Modifier.height(30.dp))

            AppTextField(email, { email = it }, "Enter your email")

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {

                    if (email.isEmpty()) {
                        Toast.makeText(context, "Enter email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->

                            isLoading = false

                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Reset link sent to your email",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.popBackStack() // back to login
                            } else {
                                Toast.makeText(
                                    context,
                                    task.exception?.message ?: "Error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4A90E2))
            ) {
                Text("Send Reset Link",color = Color.White)
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
            )
        }
    }
}