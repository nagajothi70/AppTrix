package com.example.apptrix.ui.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.models.sealed.Screen
import com.example.service.DeviceService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController) {

    val viewModel: AuthViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isPhoneVerified =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("phone_verified", false)
            ?.collectAsState()?.value ?: false

    val verifiedPhone =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("verified_phone", "")
            ?.collectAsState()?.value ?: ""

    var phone by rememberSaveable { mutableStateOf(verifiedPhone) }

    LaunchedEffect(verifiedPhone) {
        if (verifiedPhone.isNotEmpty()) {
            phone = verifiedPhone
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

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(Modifier.height(30.dp))

            AppTextField(username, {
                username = it
                errorMessage = ""
            }, "Username")

            Spacer(Modifier.height(16.dp))

            AppTextField(email, {
                email = it
                errorMessage = ""
            }, "Email", KeyboardType.Email)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                TextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        errorMessage = ""
                    },
                    placeholder = { Text("Mobile Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (phone.length < 10) {
                            errorMessage = "Enter valid phone number"
                            return@Button
                        }
                        navController.navigate("${Screen.Otp.route}/$phone")
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        if (isPhoneVerified) Color(0xFF2ECC71) else Color(0xFF4A90E2)
                    )
                ) {
                    Text(if (isPhoneVerified) "Verified" else "Verify", color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
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

            Spacer(Modifier.height(8.dp))

            ErrorText(errorMessage)

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {

                    errorMessage = ""

                    if (!isPhoneVerified) {
                        errorMessage = "Please verify your phone number"
                        return@Button
                    }

                    val validation = viewModel.validateSignup(
                        username, email, phone, password
                    )

                    if (validation.isNotEmpty()) {
                        errorMessage = validation
                        return@Button
                    }

                    isLoading = true

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->

                            isLoading = false

                            if (task.isSuccessful) {

                                val user = auth.currentUser
                                val uid = user?.uid ?: return@addOnCompleteListener

                                val deviceId = DeviceService.getDeviceId(context)

                                val userMap = hashMapOf(
                                    "email" to email,
                                    "phone" to phone,
                                    "deviceId" to deviceId
                                )

                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener {

                                        // 🔥 Email verification
                                        user.sendEmailVerification()
                                            .addOnCompleteListener { verifyTask ->

                                                if (verifyTask.isSuccessful) {

                                                    navController.navigate("${Screen.EmailVerify.route}/$email") {
                                                        popUpTo(Screen.Signup.route) { inclusive = true }
                                                    }

                                                } else {
                                                    errorMessage = "Failed to send verification email"
                                                }
                                            }
                                    }

                            } else {
                                errorMessage =
                                    task.exception?.message ?: "Signup failed"
                            }
                        }
                },
                enabled = !isLoading,
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
                    Text("Sign Up", color = Color.White)

                }
            }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Already have an account? ",
                        color = Color.LightGray
                    )

                    Text(
                        "Login",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Signup.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }

@Composable
fun appTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,

    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,

    focusedPlaceholderColor = Color.DarkGray,
    unfocusedPlaceholderColor = Color.DarkGray,

    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,

    cursorColor = Color.Black
)

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = appTextFieldColors(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ErrorText(message: String) {
    if (message.isNotEmpty()) {
        Text(
            text = message,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}