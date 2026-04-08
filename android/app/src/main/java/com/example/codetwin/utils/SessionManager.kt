package com.example.codetwin.utils

import android.content.Context

class SessionManager(context: Context) {

    private val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveTokens(access: String, refresh: String, userId: Long) {
        sharedPref.edit()
            .putString("accessToken", access)
            .putString("refreshToken", refresh)
            .putLong("userId", userId)
            .apply()
    }

    fun getUserId(): Long {
        return sharedPref.getLong("userId", -1L)
    }

    fun getAccessToken(): String? {
        return sharedPref.getString("accessToken", null)
    }

    fun getRefreshToken(): String? {
        return sharedPref.getString("refreshToken", null)
    }

    fun clearSession() {
        sharedPref.edit().clear().apply()
    }
}