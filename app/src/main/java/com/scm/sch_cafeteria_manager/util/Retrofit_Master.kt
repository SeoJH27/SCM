package com.scm.sch_cafeteria_manager.util

import android.content.Context
import android.util.Log
import com.scm.sch_cafeteria_manager.data.AdminResponse
import com.scm.sch_cafeteria_manager.data.MasterResponse
import com.scm.sch_cafeteria_manager.data.dailyMeals
import com.scm.sch_cafeteria_manager.data.requestDTO_master
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import okhttp3.Response as okResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

interface ApiService_Master {
    @Headers("Content-Type: application/json")
    @GET("/api/master/meal-plans")
    suspend fun getMealPlans(@QueryMap pendingDailyMealRequestDTO: Map<String, String>): Response<MasterResponse>

//    @Headers("Content-Type: application/json")
//    @GET("/api/master/week-meal-plans")
//    suspend fun getWeekMealPlans(@QueryMap pendingWeeklyMealRequestDTO : Map<String, String>): Response<MasterResponse>

//    @Headers("Content-Type: application/json")
//    @POST("/api/master/meal-plans/{restaurant-name}")
//    suspend fun setMealPlansMaster(@Path("restaurant-name") resName: String, @Body requestDTO_master: String): AdminResponse?

    @Multipart
    @POST("/api/master/meal-plans/{restaurant-name}")
    suspend fun setMealPlansMaster(
        @Path("restaurant-name") resName: String,
        @Part("weekStartDate") weekStartDate: RequestBody,
        @Part("dailyMeals") dailyMeals: RequestBody
    ): AdminResponse?
}

object Retrofit_Master {
    fun createApiService(context: Context): ApiService_Master {
        // Headers에 AuthInterceptor 추가
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor_Master(context))
//            .build()
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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
//suspend fun fetchWeekMealPlansMaster(
//    context: Context,
//    weekStartDate: String,
//    restaurantName: String
//): MasterResponse? {
//    val requestDTO = mapOf(
//        "weekStartDate" to weekStartDate,
//        "restaurantName" to restaurantName
//    )
//    var value: MasterResponse? = null
//    Log.e("fetchWeekMealPlansMaster", "requestDTO: $requestDTO")
//    try {
//        val response = Retrofit_Master.createApiService(context).getWeekMealPlans(requestDTO)
//        // 데이터 체크: 데이터가 없으면 null 반환
//        if (response.isSuccessful) {
//            value = response.body()
//        }else{
//            value = null
//        }
//        Log.e("fetchWeekMealPlansMaster", "응답 데이터: ${value?.data?.dailyMeal}")
//    } catch (e: Exception) {
//        Log.e("fetchWeekMealPlansMaster", "API 호출 실패: ${e.message}")
//    }
//    return value
//}

// 특정 요일 메뉴 불러오기
suspend fun fetchMealPlansMaster(
    context: Context,
    weekStartDate: String,
    dayOfWeek: String,
    restaurantName: String
): MasterResponse? {
    val requestDTO = mapOf(
        "restaurantName" to restaurantName,
        "weekStartDate" to weekStartDate,
        "dayOfWeek" to dayOfWeek
    )
    var value: MasterResponse?
    Log.e("fetchMealPlansMaster", "requestDTO: $requestDTO")
    try {
        val response = Retrofit_Master.createApiService(context).getMealPlans(requestDTO)
        // 데이터 체크: 데이터가 없으면 null 반환
        if (response.isSuccessful) {
            value = response.body()
        } else {
            value = null
        }
        Log.e("fetchMealPlansMaster", "응답 데이터: ${value?.data?.dailyMeal}")
    } catch (e: Exception) {
        Log.e("fetchMealPlansMaster", "API 호출 실패: ${e.message}")
        value = null
    }
    return value
}

//
//                                            Upload (POST)
//

// 특정 요일 메뉴 보내기
suspend fun uploadingMealPlansMaster(
    context: Context,
    restaurantName: String,
    body: requestDTO_master
): String? {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Kotlin 지원
        .build()

    // daily
    val jsonAdapter = moshi.adapter(dailyMeals::class.java)
    val jsonData = jsonAdapter.toJson(body.dailyMeals)
    val mealData = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

    // start date
    val startDateData = body.weekStartDate.toRequestBody("application/json".toMediaTypeOrNull())

    Log.e("uploadingMealPlansMaster", "Data: $mealData, $startDateData")

    try {
        val response = Retrofit_Master.createApiService(context)
            .setMealPlansMaster(restaurantName, startDateData, mealData)
        Log.e("uploadingMealPlansMaster", "응답 데이터: ${response?.status}")
        return response?.status
    } catch (e: Exception) {
        Log.e("uploadingMealPlansMaster", "API 호출 실패: ${e.message}")
        return e.message
    }
}