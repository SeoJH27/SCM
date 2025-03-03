package com.scm.sch_cafeteria_manager.util

import android.content.Context
import com.google.gson.Gson
import com.scm.sch_cafeteria_manager.data.AdminData
import com.scm.sch_cafeteria_manager.data.adminResponse
import com.scm.sch_cafeteria_manager.data.requestDTO_day
import com.scm.sch_cafeteria_manager.data.requestDTO_week
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
import retrofit2.http.Query

interface ApiService_Admin {
    @Headers("Content-Type: application/json")
    @GET("/api/admin/week-meal-plans")
    suspend fun getWeekMealPlans(@Query("pendingWeeklyMealRequestDTO") requestDTO_week: String): AdminData

    @Headers("Content-Type: application/json")
    @GET("/api/admin/meal-plans")
    suspend fun getMealPlans(@Query("pendingDailyMealRequestDTO") requestDTO_day: String): AdminData

    @Headers("Content-Type: application/json")
    @POST("/api/admin/meal-plans/{restaurant-name}")
    suspend fun setMealPlans(@Path("restaurant-name") resName: String, @Body requestDTO_day: String): adminResponse


}

object Retrofit_Admin {
    private const val BASE_URL = "http://localhost:8080" // TODO: JSON 파일의 호스트 URL

    fun createApiService(context: Context): ApiService_Admin {
        // Headers에 AuthInterceptor 추가
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor_Admin(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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

suspend fun fetchWeekMealPlans(
    context: Context,
    weekStartDate: String,
    restaurantName: String
): AdminData? {
    val requestDTO = requestDTO_week(
        weekStartDate,
        restaurantName
    )

    val jsonRequestDTO = Gson().toJson(requestDTO) // DTO를 JSON으로 변환

    try {
        val response = Retrofit_Admin.createApiService(context).getWeekMealPlans(jsonRequestDTO)
        println("응답 데이터: $response")
        return response
    } catch (e: Exception) {
        println("fetchWeekMealPlans API 호출 실패: ${e.message}")
        return null
    }
}

suspend fun fetchMealPlans(
    context: Context,
    weekStartDate: String,
    dayOfWeek: String,
    restaurantName: String,
    ): AdminData? {

    val requestDTO = requestDTO_day(
        weekStartDate,
        dayOfWeek,
        restaurantName
    )

    val jsonRequestDTO = Gson().toJson(requestDTO) // DTO를 JSON으로 변환

    try {
        val response = Retrofit_Admin.createApiService(context).getMealPlans(jsonRequestDTO)
        println("응답 데이터: $response")
        return response
    } catch (e: Exception) {
        println("fetchMealPlans API 호출 실패: ${e.message}")
        return null
    }
}

suspend fun uploadingMealPlans(
    context: Context,
    data: AdminData
){
    val jsonData = Gson().toJson(data) // data를 JSON으로 변환

    try {
        val response = Retrofit_Admin.createApiService(context).setMealPlans("HYANGSEOL1", jsonData)
        println("응답 데이터: $response")
    } catch (e: Exception) {
        println("upLoadingMealPlans API 호출 실패: ${e.message}")
    }
}