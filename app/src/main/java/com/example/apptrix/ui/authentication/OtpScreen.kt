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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun OtpScreen(navController: NavController, phone: String) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val keyboardController = LocalSoftwareKeyboardController.current

    var otp by rememberSaveable { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    var timer by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(timer) {
        if (timer > 0) {
            delay(1000)
            timer--
        } else {
            canResend = true
        }
    }

    fun sendOtp(isResend: Boolean = false) {

        message = ""

        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

                override fun onVerificationFailed(e: FirebaseException) {
                    message = e.message ?: "OTP failed ❌"
                }

                override fun onCodeSent(
                    verId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = verId
                    resendToken = token

                    message = if (isResend)
                        "OTP resent successfully ✔"
                    else
                        "OTP sent successfully ✔"
                }
            })

        if (isResend && resendToken != null) {
            optionsBuilder.setForceResendingToken(resendToken!!)
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        sendOtp(false)
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
                    keyboardController?.show() // 🔥 FIX
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

            if (message.isNotEmpty()) {
                Text(
                    message,
                    color = if (message.contains("✔")) Color.Green else Color.Red
                )
            }

            TextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 6) {
                        otp = it
                        message = ""
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

                    if (otp.length < 6) {
                        message = "Enter valid OTP"
                        return@Button
                    }

                    val verId = verificationId ?: return@Button

                    val credential = PhoneAuthProvider.getCredential(verId, otp)

                    isLoading = true

                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->

                            isLoading = false

                            if (task.isSuccessful) {

                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("phone_verified", true)

                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("verified_phone", phone)

                                navController.popBackStack()

                            } else {
                                message = "Invalid OTP ❌"
                            }
                        }
                },
                enabled = otp.length == 6 && !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4A90E2))
            ) {
                if (isLoading) {
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
                if (canResend) "Resend OTP" else "Resend in ${timer}s",
                color = if (canResend) Color.White else Color.Gray,
                modifier = Modifier.clickable(enabled = canResend) {

                    if (canResend) {
                        timer = 60
                        canResend = false
                        sendOtp(true)
                    }
                }
            )
        }
    }
}