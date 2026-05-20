package com.example.security

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences(
            "apptrix_prefs",
            Context.MODE_PRIVATE
        )

    companion object {

        private const val LOGIN_TIME = "login_time"

        private const val IS_LOGGED_IN = "is_logged_in"

        private const val UID = "uid"

        private const val USERNAME = "username"

        private const val EMAIL = "email"

        private const val ROLE = "role"

        private const val DEVICE_ID = "device_id"
    }

    // 🔥 SAVE LOGIN TIME
    fun saveLoginTime() {

        prefs.edit {

            putLong(
                LOGIN_TIME,
                System.currentTimeMillis()
            )
        }
    }

    // 🔥 GET LOGIN TIME
    fun getLoginTime(): Long {

        return prefs.getLong(
            LOGIN_TIME,
            0L
        )
    }

    // 🔥 SAVE FULL SESSION
    fun saveSession(
        uid: String,
        username: String,
        email: String,
        role: String,
        deviceId: String
    ) {

        prefs.edit {

            putBoolean(IS_LOGGED_IN, true)

            putString(UID, uid)

            putString(USERNAME, username)

            putString(EMAIL, email)

            putString(ROLE, role)

            putString(DEVICE_ID, deviceId)
        }
    }

    // 🔥 LOGIN STATUS
    fun isLoggedIn(): Boolean {

        return prefs.getBoolean(
            IS_LOGGED_IN,
            false
        )
    }

    // 🔥 GET UID
    fun getUid(): String {

        return prefs.getString(
            UID,
            ""
        ) ?: ""
    }

    // 🔥 GET USERNAME
    fun getUsername(): String {

        return prefs.getString(
            USERNAME,
            ""
        ) ?: ""
    }

    // 🔥 GET EMAIL
    fun getEmail(): String {

        return prefs.getString(
            EMAIL,
            ""
        ) ?: ""
    }

    // 🔥 GET ROLE
    fun getRole(): String {

        return prefs.getString(
            ROLE,
            ""
        ) ?: ""
    }

    // 🔥 GET DEVICE ID
    fun getDeviceId(): String {

        return prefs.getString(
            DEVICE_ID,
            ""
        ) ?: ""
    }

    // 🔥 CLEAR SESSION
    fun clearSession() {

        prefs.edit {

            clear()
        }
    }
}
