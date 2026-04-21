package com.example.apptrix


import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.apptrix.ui.theme.ApptrixTheme

class MainActivity : androidx.fragment.app.FragmentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApptrixTheme {
                AppNav()
            }
        }
    }
}