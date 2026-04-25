package com.example.apptrix.ui.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.models.sealed.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import com.example.apptrix.R

@Composable
fun EmailVerificationScreen(
    navController: NavController,
    email: String
) {

    val auth = FirebaseAuth.getInstance()

    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    // 🔥 TIMER STATES
    var canResend by remember { mutableStateOf(false) }
    var timer by remember { mutableIntStateOf(90) }

    // 🔥 IMPORTANT: trigger for restarting timer
    var restartTimer by remember { mutableStateOf(0) }

    // 🔥 FIXED TIMER LOGIC
    LaunchedEffect(restartTimer) {
        canResend = false
        timer = 90   // 👉 change to 300 if you want 5 min

        while (timer > 0) {
            delay(1000)
            timer--
        }

        canResend = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D2A5C), Color(0xFF123E7C))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            // 🔥 EMAIL LOGO
            Image(
                painter = painterResource(id = R.drawable.email),
                contentDescription = "Email",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(50.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Verify your Email",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = email,
                color = Color(0xFFB0C4DE),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We sent a verification link to your email. Please verify to continue.",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔥 MESSAGE
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = Color.Yellow,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 🔥 RESEND BUTTON
            Button(
                onClick = {

                    val user = auth.currentUser

                    if (user == null) {
                        message = "Session expired. Please signup again."
                        return@Button
                    }

                    isSending = true

                    user.reload().addOnCompleteListener {

                        user.sendEmailVerification()
                            .addOnCompleteListener { task ->

                                isSending = false

                                if (task.isSuccessful) {
                                    message = "Verification email sent ✔"

                                    // 🔥 restart timer properly
                                    restartTimer++
                                } else {
                                    message =
                                        task.exception?.message ?: "Failed to resend ❌"
                                }
                            }
                    }
                },
                enabled = canResend && !isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {

                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    if (!canResend) {
                        Text("Resend in ${timer}s")
                    } else {
                        Text("Resend Email")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 GO TO LOGIN
            Button(
                onClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4A90E2))
            ) {
                Text("Go to Login", color = Color.White)
            }
        }
    }
}