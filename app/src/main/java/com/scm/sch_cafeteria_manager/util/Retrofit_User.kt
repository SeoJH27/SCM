package com.scm.sch_cafeteria_manager.util

import android.util.Log
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.scm.sch_cafeteria_manager.data.UserDetailResponse
import com.scm.sch_cafeteria_manager.data.UserTodayMenuResponse
import com.scm.sch_cafeteria_manager.data.VersionResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

interface ApiService_User {
    @Headers("Content-Type: application/json")
    @GET("/api/user/meal-plans/detail")
    suspend fun getDetailMenu(@QueryMap request: Map<String, String>): Response<UserDetailResponse>

    @Headers("Content-Type: application/json")
    @GET("/api/user/meal-plans/today")
    suspend fun getTodayMenu(@QueryMap request: Map<String, String>): Response<UserTodayMenuResponse>

    @Headers("Content-Type: application/json")
    @GET("/api/version")
    suspend fun getVersion(): Response<VersionResponse>
}

object RetrofitClient_User {
    fun createApiService(): ApiService_User {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(ApiService_User::class.java)
    }
}

suspend fun fetchDetailMenu(restaurantName: String, weekStartDate: String): UserDetailResponse? {
    Log.e("DetailHs1Fragment", "fetchDetailMenu")

    val request = mapOf(
        "restaurantName" to restaurantName,
        "weekStartDate" to weekStartDate
    )
    var value: UserDetailResponse?
    Log.e("fetchDetailMenu", "request=${request}")

    try {
        val data = RetrofitClient_User.createApiService()
            .getDetailMenu(request) // suspend 사용으로 awaitResponse 불필요

        // 데이터 체크: 데이터가 없으면 null 반환
        value = if (data.isSuccessful) {
            data.body()
        } else {
            null
        }
        Log.e("fetchDetailMenu", "응답 데이터: ${value?.data?.dailyMeals}")
    } catch (e: Exception) {
        Log.e("fetchDetailMenu", "Network Error: ${e.message}")
        value = null
    }
    return value
}

// day: 오늘 요일
suspend fun fetchTodayMenu(dayOfWeek: String, weekStartDate: String): UserTodayMenuResponse? {
    Log.e("TodayMenuFragment", "fetchTodayMenu")

    val request = mapOf(
        "dayOfWeek" to dayOfWeek,
        "weekStartDate" to weekStartDate
    )

    var value: UserTodayMenuResponse?

    try {
        val data = RetrofitClient_User.createApiService()
            .getTodayMenu(request) // suspend 사용, awaitResponse 불필요
        // 데이터 체크: 데이터가 없으면 null 반환
        value = if (data.isSuccessful) {
            data.body()
        } else {
            null
        }
        Log.e("fetchTodayMenu", "응답 데이터: ${value?.data}")
    } catch (e: Exception) {
        Log.e("fetchTodayMenu", "API 호출 실패: ${e.message}")
        value = null
    }
    return value
}

suspend fun fetchVersion(): VersionResponse? {
    Log.e("SCM", "fetchVersion")
    var value: VersionResponse?

    try {
        val data = RetrofitClient_User.createApiService()
            .getVersion()
        // 데이터 체크: 데이터가 없으면 null 반환
        value = if (data.isSuccessful) {
            data.body()
        } else {
            null
        }
        Log.e("fetchTodayMenu", "응답 데이터: $value")
    } catch (e: Exception) {
        Log.e("fetchTodayMenu", "API 호출 실패: ${e.message}")
        value = null
    }
    return value
}