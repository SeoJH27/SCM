package com.scm.sch_cafeteria_manager.util

import android.util.Log
import com.scm.sch_cafeteria_manager.data.BASE_URL
import com.scm.sch_cafeteria_manager.data.TodayMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService_TM {
    @Headers("Content-Type: application/json")
    @GET("user.json") // TODO: 방식 설정
    suspend fun getTodayMenu(): TodayMenu
}

object RetrofitClient_TM {

    val apiService: ApiService_TM by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService_TM::class.java)
    }

}

suspend fun fetchTodayMenu(): TodayMenu? {
    return withContext(Dispatchers.IO) {
        try {
            RetrofitClient_TM.apiService.getTodayMenu() // suspend 사용으로 awaitResponse 불필요
        } catch (e: Exception) {
            Log.e("fetchMenu", "Network Error: ${e.message}")
            null
        }
    }
}
