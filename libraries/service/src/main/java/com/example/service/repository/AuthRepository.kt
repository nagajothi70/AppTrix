package com.example.service.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): String
}
