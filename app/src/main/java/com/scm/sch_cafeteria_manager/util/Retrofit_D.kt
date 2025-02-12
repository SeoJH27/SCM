package com.scm.sch_cafeteria_manager.util

import com.scm.sch_cafeteria_manager.data.DetailMenu
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService_D {
    @GET("user.json") // TODO: 방식 설정
    suspend fun getDetailMenu(): DetailMenu
}

object RetrofitClient_D {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://example.com/") // TODO: JSON 파일의 호스트 URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService_D = retrofit.create(ApiService_D::class.java)
}
