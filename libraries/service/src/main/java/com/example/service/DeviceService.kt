package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

object DeviceService {

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}