package com.scm.sch_cafeteria_manager.util

import android.content.Context

object PrefHelper_Login {

    fun saveAccessToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("accessToken", token).apply()
    }

    fun getAccessToken(context: Context): String? {
        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }

}