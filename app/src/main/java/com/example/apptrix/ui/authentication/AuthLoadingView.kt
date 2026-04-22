package com.example.apptrix.ui.authentication

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.models.sealed.Screen
import androidx.compose.ui.graphics.Color as ComposeColor
import kotlinx.coroutines.delay


@Composable
fun AuthLoadingScreen(navController: NavController) {

    LaunchedEffect(Unit) {

        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.AuthLoading.route) { inclusive = true }
        }
    }

    AuthLoadingView()
}
@Composable
fun AuthLoadingView() {

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
        CircularProgressIndicator(
            color = ComposeColor.White,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )
    }
}