package com.example.apptrix


import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import com.example.apptrix.ui.theme.ApptrixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : androidx.fragment.app.FragmentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setContent {
            ApptrixTheme {
                AppNav()
            }
        }
    }
}
