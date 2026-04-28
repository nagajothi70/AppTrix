package com.example.service

import com.example.service.FirebaseService
import com.example.service.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : AuthRepository {

    override suspend fun login(email: String, password: String): String {
        return firebaseService.loginUser(email, password).getOrNull() ?: "Error"
    }
}
