package com.scm.sch_cafeteria_manager.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.scm.sch_cafeteria_manager.data.AdminData
import com.scm.sch_cafeteria_manager.data.MasterData
import com.scm.sch_cafeteria_manager.data.adminResponse
import com.scm.sch_cafeteria_manager.data.requestDTO_week_master
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import retrofit2.Response
import okhttp3.Response as okResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService_Master {
    @Headers("Content-Type: application/json")
    @POST("/api/master/meal-plans")
    suspend fun getMealPlans(@QueryMap pendingDailyMealRequestDTO: Map<String, String>): Response<MasterData>

    @Headers("Content-Type: application/json")
    @POST("/api/master/week-meal-plans")
    suspend fun getWeekMealPlans(@QueryMap pendingWeeklyMealRequestDTO : Map<String, String>): Response<MasterData>

    @Headers("Content-Type: application/json")
    @POST("/api/admin/meal-plans/{restaurant-name}")
    suspend fun setMealPlansMaster(@Path("restaurant-name") resName: String, @Body requestDTO_week_master: String): adminResponse
}

object Retrofit_Master {
    fun createApiService(context: Context): ApiService_Master {
        // Headers에 AuthInterceptor 추가
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor_Master(context))
//            .build()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService_Master::class.java)
    }
}

private class AuthInterceptor_Master(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okResponse {
        val token = PrefHelper_Login.getAccessToken(context) // SharedPreferences에서 토큰 불러오기
        val request = chain.request().newBuilder()

        token?.let {
            request.addHeader("Authorization", "Bearer $it") // Authorization 헤더 추가
        }
        return chain.proceed(request.build())
    }
}

//
//                                            fetch (GET)
//

// 일주일 메뉴 불러오기
suspend fun fetchWeekMealPlansMaster(
    context: Context,
    weekStartDate: String,
    restaurantName: String
): MasterData? {
    val requestDTO = mapOf(
        "weekStartDate" to weekStartDate,
        "restaurantName" to restaurantName
    )
    var value: MasterData? = null
    Log.e("fetchWeekMealPlansMaster", "requestDTO: $requestDTO")
    try {
        val response = Retrofit_Master.createApiService(context).getWeekMealPlans(requestDTO)
        // 데이터 체크: 데이터가 없으면 null 반환
        if (response.isSuccessful) {
            value = response.body()
        }else{
            value = null
        }
        Log.e("fetchWeekMealPlansMaster", "응답 데이터: ${value?.data?.dailyMeal}")
    } catch (e: Exception) {
        Log.e("fetchWeekMealPlansMaster", "API 호출 실패: ${e.message}")
    }
    return value
}

// 특정 요일 메뉴 불러오기
suspend fun fetchMealPlansMaster(
    context: Context,
    weekStartDate: String,
    dayOfWeek: String,
    restaurantName: String
): MasterData? {
    val requestDTO = mapOf(
        "weekStartDate" to weekStartDate,
        "dayOfWeek" to dayOfWeek,
        "restaurantName" to restaurantName
    )
    var value: MasterData? = null
    Log.e("fetchMealPlansMaster", "requestDTO: $requestDTO")
    try {
        val response = Retrofit_Master.createApiService(context).getMealPlans(requestDTO)
        // 데이터 체크: 데이터가 없으면 null 반환
        if (response.isSuccessful) {
            value = response.body()
        }else{
            value = null
        }
        Log.e("fetchMealPlansMaster", "응답 데이터: ${value?.data?.dailyMeal}")
    } catch (e: Exception) {
        Log.e("fetchMealPlansMaster", "API 호출 실패: ${e.message}")
    }
    return value
}

//
//                                            Upload (POST)
//

// 일주일 메뉴 불러오기
suspend fun uploadingWeekMealPlansMaster(
    context: Context,
    restaurantName: String,
    body: requestDTO_week_master
): adminResponse? {
    val jsonData = Gson().toJson(body) // data를 JSON으로 변환

    Log.e("uploadingWeekMealPlansMaster", "jsonData: $jsonData")

    try {
        val response = Retrofit_Master.createApiService(context).setMealPlansMaster(restaurantName, jsonData)
        Log.e("uploadingWeekMealPlansMaster", "응답 데이터: $response")
        return response
    } catch (e: Exception) {
        Log.e("uploadingWeekMealPlansMaster", "API 호출 실패: ${e.message}")
        return null
    }
}