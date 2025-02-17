package com.scm.sch_cafeteria_manager.util

import android.util.Log
import com.scm.sch_cafeteria_manager.data.DetailMenu
import com.scm.sch_cafeteria_manager.data.TodayMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService_D {
    @Headers("Content-Type: application/json")
    @GET("user.json") // TODO: 방식 설정
    suspend fun getDetailMenu(): DetailMenu
}

object RetrofitClient_D {
    private const val BASE_URL = "https://example.com/api/" // TODO: JSON 파일의 호스트 URL

    val apiService: ApiService_D by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService_D::class.java)
    }
}

suspend fun fetchDetailMenu(): DetailMenu? {
    return withContext(Dispatchers.IO) {
        try {
            RetrofitClient_D.apiService.getDetailMenu() // suspend 사용으로 awaitResponse 불필요
        } catch (e: Exception) {
            Log.e("fetchMenu", "Network Error: ${e.message}")
            null
        }
    }
}
