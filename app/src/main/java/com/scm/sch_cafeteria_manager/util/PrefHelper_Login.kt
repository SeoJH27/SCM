package com.scm.sch_cafeteria_manager.util

import android.content.Context

object PrefHelper_Login {

    fun saveTokens(context: Context, accessTk: String, refreshTk: String) {
        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("accessToken", accessTk)
            putString("refreshToken", refreshTk)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }

    fun getRefreshToken(context: Context): String? {
        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return prefs.getString("refreshToken", null)
    }

}