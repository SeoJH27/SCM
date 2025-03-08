package com.scm.sch_cafeteria_manager.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.scm.sch_cafeteria_manager.data.AdminData
import com.scm.sch_cafeteria_manager.data.adminResponse
import com.scm.sch_cafeteria_manager.data.requestDTO_dayOfWeek
import com.scm.sch_cafeteria_manager.data.requestDTO_week
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService_Admin {
    @Headers("Content-Type: application/json")
    @GET("/api/admin/meal-plans")
    suspend fun getMealPlans(@QueryMap pendingDailyMealRequestDTO: Map<String, String>): AdminData

    @Headers("Content-Type: application/json")
    @GET("/api/admin/week-meal-plans")
    suspend fun getWeekMealPlans(@QueryMap pendingWeeklyMealRequestDTO: Map<String, String>): AdminData

    @Headers("Content-Type: application/json")
    @POST("/api/admin/week-meal-plans/{restaurant-name}")
    suspend fun setWeekMealPlans(
        @Path("restaurant-name") resName: String,
        @Body requestDTO_dayOfWeek: String
    ): adminResponse

    @Headers("Content-Type: application/json")
    @POST("/api/admin/meal-plans/{restaurant-name}/{day-of-week}")
    suspend fun setMealPlans(
        @Path("restaurant-name") resName: String,
        @Path("day-of-week") dayOfWeek: String,
        @Body requestDTO_week: String
    ): adminResponse
}

object Retrofit_Admin {

    fun createApiService(context: Context): ApiService_Admin {
        // Headers에 AuthInterceptor 추가
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor_Admin(context))
//            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService_Admin::class.java)
    }
}

private class AuthInterceptor_Admin(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = PrefHelper_Login.getAccessToken(context) // SharedPreferences에서 토큰 불러오기
        val request = chain.request().newBuilder()

        token?.let {
            request.addHeader("Authorization", "Bearer $it") // Authorization 헤더 추가
        }
        return chain.proceed(request.build())
    }
}

// 일주일 메뉴 불러오기
suspend fun fetchWeekMealPlans(
    context: Context,
    weekStartDate: String,
    restaurantName: String
): AdminData? {
    val requestDTO = mapOf(
        "weekStartDate" to weekStartDate,
        "restaurantName" to restaurantName
    )

    Log.e("fetchWeekMealPlans", "requestDTO: $requestDTO")

    try {
        val response = Retrofit_Admin.createApiService(context).getWeekMealPlans(requestDTO)
        Log.e("fetchWeekMealPlans", "응답 데이터: $response")
        return response
    } catch (e: Exception) {
        Log.e("fetchWeekMealPlans", "API 호출 실패: ${e.message}")
        return null
    }
}

// 특정 요일 메뉴 불러오기
suspend fun fetchMealPlans(
    context: Context,
    restaurantName: String,
    dayOfWeek: String,
    weekStartDate: String,
): AdminData? {

    val requestDTO = mapOf(
        "restaurantName" to restaurantName,
        "dayOfWeek" to dayOfWeek,
        "weekStartDate" to weekStartDate
    )

    Log.e("fetchMealPlans", "requestDTO: $requestDTO")

    try {
        val response = Retrofit_Admin.createApiService(context).getMealPlans(requestDTO)
        Log.e("fetchMealPlans", "응답 데이터: $response")
        return response
    } catch (e: Exception) {
        Log.e("fetchMealPlans", "API 호출 실패: ${e.message}")
        return null
    }
}

// 일주일 메뉴 모두 업로드 하기
suspend fun uploadingWeekMealPlans(
    context: Context,
    body: requestDTO_week
) {
    val jsonData = Gson().toJson(body) // data를 JSON으로 변환

    try {
        val response =
            Retrofit_Admin.createApiService(context).setWeekMealPlans("HYANGSEOL1", jsonData)
        println("응답 데이터: $response")
    } catch (e: Exception) {
        println("upLoadingMealPlans API 호출 실패: ${e.message}")
    }
}


// 특정 요일 메뉴 업로드하기
suspend fun uploadingMealPlans(
    context: Context,
    dayOfWeek: String,
    body: requestDTO_dayOfWeek
) {
    val jsonData = Gson().toJson(body) // data를 JSON으로 변환

    try {
        val response =
            Retrofit_Admin.createApiService(context).setMealPlans("HYANGSEOL1", dayOfWeek, jsonData)
        println("응답 데이터: $response")
    } catch (e: Exception) {
        println("upLoadingMealPlans API 호출 실패: ${e.message}")
    }
}