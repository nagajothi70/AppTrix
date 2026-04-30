package com.example.apptrix.ui.authentication

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OtpScreen(
    navController: NavController,
    phone: String,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context as Activity
    val keyboardController = LocalSoftwareKeyboardController.current

    val state by viewModel.otpState.collectAsState()

    var otp by rememberSaveable { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    // 🔥 First time OTP send
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.sendOtp(phone, activity)
    }

    // 🔥 Handle success
    LaunchedEffect(state.isVerified) {
        if (state.isVerified) {

            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("phone_verified", true)

            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("verified_phone", phone)

            navController.popBackStack()
        }
    }

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

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {

            Text(
                "Enter OTP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "Sent to +91 $phone",
                color = Color.LightGray
            )

            Spacer(Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            ) {
                repeat(6) { index ->
                    val char = otp.getOrNull(index)?.toString() ?: ""

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(char, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ✅ INLINE MESSAGE (SAFE + NO CRASH)
            if (state.message.isNotEmpty()) {

                val messageLower = state.message.lowercase()

                val messageColor =
                    if (messageLower.contains("invalid")) Color.Red
                    else Color(0xFF2ECC71)

                Text(
                    text = state.message,
                    color = messageColor,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            TextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 6) {
                        otp = it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .size(1.dp)
                    .alpha(0f)
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {
                    viewModel.verifyOtp(otp)
                },
                enabled = otp.length == 6 && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4A90E2))
            ) {

                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Verify OTP", color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                if (state.canResend) "Resend OTP" else "Resend in ${state.timer}s",
                color = if (state.canResend) Color.White else Color.Gray,
                modifier = Modifier.clickable(enabled = state.canResend) {
                    viewModel.sendOtp(phone, activity, true)
                }
            )
        }
    }
}
