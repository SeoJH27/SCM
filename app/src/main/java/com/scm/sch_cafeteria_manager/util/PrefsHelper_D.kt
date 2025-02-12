package com.scm.sch_cafeteria_manager.util

import android.content.Context
import com.scm.sch_cafeteria_manager.data.TodayMenu
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PrefsHelper_D {

    private const val PREFS_NAME = "AppPrefs"
    private const val USER_KEY = "D_data"

    // ✅ 데이터 저장
    fun saveUser(context: Context, todayMenu: TodayMenu) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Json.encodeToString(todayMenu) // 직렬화
        prefs.edit().putString(USER_KEY, json).apply() // 저장
    }

    // ✅ 데이터 불러오기
    fun getUser(context: Context): TodayMenu? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(USER_KEY, null)
        return json?.let { Json.decodeFromString(it) } // 역직렬화
    }

    // ✅ 데이터 삭제
    fun clearUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(USER_KEY).apply()
    }
}