package com.scm.sch_cafeteria_manager.util

import android.util.Log
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.scm.sch_cafeteria_manager.data.TM_API_Response
import com.scm.sch_cafeteria_manager.data.TodayMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiService_TM {
    @Headers("Content-Type: application/json")
    @GET("/api/user/meal-plans/today/{day-of-week}") // TODO: 방식 설정
    suspend fun getTodayMenu(@Path("day-of-week") dayOfWeek: String): TM_API_Response
}

object RetrofitClient_TM {

    val apiService: ApiService_TM by lazy {
        try {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService_TM::class.java)
        } catch (e: Exception) {
            Log.e("RetrofitClient_TM", "ApiService_TM - Retrofit 초기화 실패: ${e.message}")
            throw RuntimeException("Retrofit 초기화 실패 ${e.message}")
        }
    }

}

// day: 오늘 요일
suspend fun fetchTodayMenu(day: String): TM_API_Response? {
    Log.e("TodayMenuFragment", "fetchTodayMenu")

    return withContext(Dispatchers.IO) {
        Log.e("fetchTodayMenu", "withContext")
        try {
            Log.e("fetchTodayMenu", "$BASE_URL/api/user/meal-plans/today/$day")
            RetrofitClient_TM.apiService.getTodayMenu(day) // suspend 사용으로 awaitResponse 불필요
        } catch (e: Exception) {
            Log.e("fetchTodayMenu", "Network Error: ${e.message}")
            null
        }
    }

}
