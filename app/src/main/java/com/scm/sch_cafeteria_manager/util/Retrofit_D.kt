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

interface ApiService_D_hs1 {
    @Headers("Content-Type: application/json")
    @GET("/api/user/meal-plans/detail/HYANGSEOL1") // TODO: 방식 설정
    suspend fun getDetailMenu(): DetailMenu
}

interface ApiService_D_staff {
    @Headers("Content-Type: application/json")
    @GET("/api/user/meal-plans/detail/FACULTY") // TODO: 방식 설정
    suspend fun getDetailMenu(): DetailMenu
}

object RetrofitClient_D {
    private const val BASE_URL = "http://localhost:8080" // TODO: JSON 파일의 호스트 URL

    val apiService_hs1: ApiService_D_hs1 by lazy {
        Log.e("RetrofitClient_D", "http://localhost:8080/api/user/meal-plans/detail/HYANGSEOL1")
        try {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService_D_hs1::class.java)
        } catch (e: Exception) {
            Log.e("RetrofitClient_D", "Retrofit 초기화 실패: ${e.message}")
            throw RuntimeException("Retrofit 초기화 실패: ${e.message}")
        }
    }

    val apiService_staff: ApiService_D_staff by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService_D_staff::class.java)
    }
}

suspend fun fetchDetailMenu(boolean: Boolean): DetailMenu? {
    Log.e("DetailHs1Fragment", "fetchDetailMenu")

    if (boolean) {
        return withContext(Dispatchers.IO) {
            Log.e("DetailHs1Fragment", "Hs1 - withContext - if")

            try {
                Log.e("DetailHs1Fragment", "Hs1 - RetrofitClient_D.apiService_hs1.getDetailMenu()")
                RetrofitClient_D.apiService_hs1.getDetailMenu() // suspend 사용으로 awaitResponse 불필요
            } catch (e: Exception) {
                Log.e("fetchMenu", "Network Error: ${e.message}")
                null
            }
        }
    } else {
        return withContext(Dispatchers.IO) {
            Log.e("DetailHs1Fragment", "Staff - withContext - else")
            try {
                Log.e(
                    "DetailHs1Fragment",
                    "Staff - RetrofitClient_D.apiService_hs1.getDetailMenu()"
                )
                RetrofitClient_D.apiService_staff.getDetailMenu() // suspend 사용으로 awaitResponse 불필요
            } catch (e: Exception) {
                Log.e("fetchMenu", "Network Error: ${e.message}")
                null
            }
        }
    }

}
