package com.example.service

import javax.inject.Inject
class FirebaseService @Inject constructor() {

    suspend fun loginUser(email: String, password: String): Result<String> {
        return Result.success("Success")
    }
}
