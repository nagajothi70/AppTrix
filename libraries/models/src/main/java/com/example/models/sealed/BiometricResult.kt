package com.example.models.sealed

sealed class BiometricResult {
    object Success : BiometricResult()
    object Failed : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}
