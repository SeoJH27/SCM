package com.scm.sch_cafeteria_manager.util

import android.content.Context
import com.scm.sch_cafeteria_manager.data.UserRole
import java.io.File

object cacheHelper {

    fun saveToCache(context: Context, fileName: String, data: UserRole) {
        val file = File(context.cacheDir, fileName)
        file.writeText(data.toString())
    }

    fun readFromCache(context: Context, fileName: String): String? {
        val file = File(context.cacheDir, fileName)
        return if (file.exists()) file.readText() else null
    }

    fun clearCache(context: Context) {
        val cacheDir = context.cacheDir
        cacheDir.deleteRecursively() // 모든 캐시 파일 삭제
    }
}