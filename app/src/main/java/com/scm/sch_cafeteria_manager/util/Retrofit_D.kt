package com.scm.sch_cafeteria_manager.util

import android.util.Log
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.scm.sch_cafeteria_manager.data.D_API_Response
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
    @GET("/api/user/meal-plans/detail/HYANGSEOL1")
    suspend fun getDetailMenu_hs1(): D_API_Response

    @Headers("Content-Type: application/json")
    @GET("/api/user/meal-plans/detail/FACULTY")
    suspend fun getDetailMenu_staff(): D_API_Response
}

object RetrofitClient_D {

    val apiService: ApiService_D by lazy {
        try {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService_D::class.java)
        } catch (e: Exception) {
            Log.e("RetrofitClient_D", "apiService_hs1 - Retrofit 초기화 실패: ${e.message}")
            throw RuntimeException("Retrofit 초기화 실패: ${e.message}")
        }
    }

}

suspend fun fetchDetailMenu(boolean: Boolean): D_API_Response? {
    Log.e("DetailHs1Fragment", "fetchDetailMenu")

    // boolean이 true면 hs1, false면 staff
    if (boolean) {
        return withContext(Dispatchers.IO) {
            Log.e("fetchDetailMenu", "Hs1 - withContext - if")
            try {
                Log.e("fetchDetailMenu", "$BASE_URL/api/user/meal-plans/detail/HYANGSEOL1")
                RetrofitClient_D.apiService.getDetailMenu_hs1() // suspend 사용으로 awaitResponse 불필요
            } catch (e: Exception) {
                Log.e("fetchDetailMenu", "Network Error: ${e.message}")
                null
            }
        }
    } else {
        return withContext(Dispatchers.IO) {
            Log.e("fetchDetailMenu", "Staff - withContext - else")
            try {
                Log.e(
                    "fetchDetailMenu",
                    "$BASE_URL/api/user/meal-plans/detail/FACULTY"
                )
                RetrofitClient_D.apiService.getDetailMenu_staff() // suspend 사용으로 awaitResponse 불필요
            } catch (e: Exception) {
                Log.e("fetchDetailMenu", "Network Error: ${e.message}")
                null
            }
        }
    }

}
