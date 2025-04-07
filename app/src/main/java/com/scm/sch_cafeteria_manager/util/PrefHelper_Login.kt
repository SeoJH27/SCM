package com.scm.sch_cafeteria_manager.util

import android.content.Context

object PrefHelper_Login {

    fun saveTokens(context: Context, accessTk: String, refreshTk: String, authority: String) {
        val prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("accessToken", accessTk)
            putString("refreshToken", refreshTk)
            putBoolean("isLoggedIn", true)
            putString("authority", authority)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        val prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }

    fun getRefreshToken(context: Context): String? {
        val prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return prefs.getString("refreshToken", null)
    }

    fun getIsLoggedIn(context: Context): Boolean{
        val prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("isLoggedIn", false)
    }

    fun getAuthority(context: Context): String?{
        val prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return prefs.getString("authority", null)
    }

    fun resetToken(context: Context, accessTk: String, refreshTk: String){
        val prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("accessToken", accessTk)
            putString("refreshToken", refreshTk)
            apply()
        }
    }

    fun deleteTokens(context: Context){
        val prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}