package com.example.apptrix.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apptrix.ui.authentication.biometric.BiometricViewModel
import com.example.models.sealed.BiometricResult
import com.example.models.ui.Screen

@Composable
fun BiometricScreen(
    navController: NavController,
    viewModel: BiometricViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val state by viewModel.state.collectAsState()

    // 🔥 Handle result
    LaunchedEffect(state) {
        when (state) {
            is BiometricResult.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Biometric.route) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is BiometricResult.Failed -> {
                Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show()
            }

            is BiometricResult.Error -> {
                Toast.makeText(
                    context,
                    (state as BiometricResult.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    // 🔥 Auto trigger
    LaunchedEffect(Unit) {
        activity?.let { viewModel.authenticate(it) }
    }

    // 🔥 YOUR ORIGINAL UI (UNCHANGED)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D2A5C), Color(0xFF123E7C))
                )
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Lock",
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(60.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Verify Your Identity",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Use your fingerprint to securely continue",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                Color(0x114A90E2),
                                shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Fingerprint,
                            contentDescription = "Fingerprint",
                            tint = Color(0xFF4A90E2),
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            activity?.let { viewModel.authenticate(it) }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF4A90E2))
                    ) {
                        Text("Scan Fingerprint", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Biometric.route) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Use password instead", color = Color.Gray)
                    }
                }
            }
        }
    }
}
