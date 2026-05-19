package com.example.apptrix

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.apptrix.ui.theme.ApptrixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : androidx.fragment.app.FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 Disable screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // 🔥 Fullscreen without white top space
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 🔥 Transparent status bar
        window.statusBarColor = Color.TRANSPARENT

        val startDestination =
            intent.getStringExtra("startDestination") ?: "login"

        val email = intent.getStringExtra("email") ?: ""

        setContent {
            ApptrixTheme {
                AppNav(startDestination, email)
            }
        }
    }
}
