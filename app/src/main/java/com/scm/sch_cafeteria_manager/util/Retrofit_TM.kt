package com.scm.sch_cafeteria_manager.util

import com.scm.sch_cafeteria_manager.data.TodayMenu
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService_TM {
    @GET("user.json") // TODO: 방식 설정
    suspend fun getTodayMenu(): TodayMenu
}

object RetrofitClient_TM {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://example.com/") // TODO: JSON 파일의 호스트 URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService_TM = retrofit.create(ApiService_TM::class.java)
}
