package com.example.security

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("apptrix_prefs", Context.MODE_PRIVATE)

    fun saveLoginTime() {
        prefs.edit { putLong("login_time", System.currentTimeMillis()) }
    }

    fun getLoginTime(): Long {
        return prefs.getLong("login_time", 0L)
    }

    fun clearSession() {
        prefs.edit { clear() }
    }
}