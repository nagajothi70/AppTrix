package com.example.apptrix

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import android.graphics.Color

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 Fullscreen fix
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 🔥 Transparent status bar
        window.statusBarColor = Color.TRANSPARENT

        setContent {
            SplashScreen {
                handleNavigation()
            }
        }
    }

    private fun handleNavigation() {

        val user = FirebaseAuth.getInstance().currentUser

        val intent = when {
            user == null -> {
                Intent(this, MainActivity::class.java).apply {
                    putExtra("startDestination", "login")
                }
            }

            !user.isEmailVerified -> {
                Intent(this, MainActivity::class.java).apply {
                    putExtra("startDestination", "email_verification")
                    putExtra("email", user.email ?: "")
                }
            }

            else -> {
                Intent(this, MainActivity::class.java).apply {
                    putExtra("startDestination", "biometric")
                }
            }
        }

        startActivity(intent)
        finish()
    }
}
@Composable
fun SplashScreen(onFinish: () -> Unit) {

    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(true) {
        alpha.animateTo(1f, tween(1000))
        scale.animateTo(1f, tween(1000))
        delay(1200)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        ComposeColor(0xFF0D2A5C),
                        ComposeColor(0xFF123E7C)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "AppTrix",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = ComposeColor.White,
                modifier = Modifier
                    .alpha(alpha.value)
                    .scale(scale.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Learn. Grow. Succeed.",
                color = ComposeColor.LightGray,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(60.dp))

            CircularProgressIndicator(
                color = ComposeColor.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
