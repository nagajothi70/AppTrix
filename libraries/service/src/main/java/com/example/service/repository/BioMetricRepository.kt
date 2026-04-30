package com.example.service.repository

import androidx.fragment.app.FragmentActivity
import com.example.models.sealed.BiometricResult

interface BiometricRepository {
    fun authenticate(
        activity: FragmentActivity,
        onResult: (BiometricResult) -> Unit
    )
}
