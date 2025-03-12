package com.scm.sch_cafeteria_manager.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.scm.sch_cafeteria_manager.data.AdminResponse
import com.scm.sch_cafeteria_manager.data.api_response
import com.scm.sch_cafeteria_manager.data.requestDTO_dayOfWeek
import com.scm.sch_cafeteria_manager.data.requestDTO_week
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.Response as okResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService_Admin {
    @Headers("Content-Type: application/json")
    @GET("/api/admin/meal-plans")
    suspend fun getMealPlans(@QueryMap pendingDailyMealRequestDTO: Map<String, String>): Response<AdminResponse>

//    @Headers("Content-Type: application/json")
//    @GET("/api/admin/week-meal-plans")
//    suspend fun getWeekMealPlans(@QueryMap pendingWeeklyMealRequestDTO: Map<String, String>): AdminData

    @Headers("Content-Type: application/json")
    @POST("/api/admin/week-meal-plans/{restaurant-name}")
    suspend fun setWeekMealPlans(
        @Path("restaurant-name") resName: String,
        @Body requestDTO_dayOfWeek: String
    ): Response<AdminResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/admin/meal-plans/{restaurant-name}/{day-of-week}")
    suspend fun setMealPlans(
        @Path("restaurant-name") resName: String,
        @Path("day-of-week") dayOfWeek: String,
        @Body requestDTO_week: String
    ): Response<AdminResponse>
}

object Retrofit_Admin {
    fun createApiService(context: Context): ApiService_Admin {
        // Headers에 AuthInterceptor 추가
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor_Admin(context))
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
            .create(ApiService_Admin::class.java)
    }
}

private class AuthInterceptor_Admin(private val context: Context) : Interceptor {
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
//suspend fun fetchWeekMealPlans(
//    context: Context,
//    weekStartDate: String,
//    restaurantName: String
//): AdminData? {
//    val requestDTO = mapOf(
//        "weekStartDate" to weekStartDate,
//        "restaurantName" to restaurantName
//    )
//
//    Log.e("fetchWeekMealPlans", "requestDTO: $requestDTO")
//
//    try {
//        val response = Retrofit_Admin.createApiService(context).getWeekMealPlans(requestDTO)
//        Log.e("fetchWeekMealPlans", "응답 데이터: ${response.message}")
//        return response
//    } catch (e: Exception) {
//        Log.e("fetchWeekMealPlans", "API 호출 실패: ${e.message}")
//        return null
//    }
//}

// 특정 요일 메뉴 불러오기
suspend fun fetchMealPlans(
    context: Context,
    restaurantName: String,
    dayOfWeek: String,
    weekStartDate: String,
): AdminResponse? {
    val requestDTO = mapOf(
        "restaurantName" to restaurantName,
        "dayOfWeek" to dayOfWeek,
        "weekStartDate" to weekStartDate
    )
    var value: AdminResponse?

    Log.e("fetchMealPlans", "requestDTO: $requestDTO")
    try {
        val data = Retrofit_Admin.createApiService(context).getMealPlans(requestDTO)

        // 데이터 체크: 데이터가 없으면 null 반환
        if (data.isSuccessful) {
            value = data.body()
        }else{
            value = null
        }
        Log.e("fetchMealPlans", "응답 데이터: ${value?.data?.dailyMeal}")
    } catch (e: Exception) {
        Log.e("fetchMealPlans", "API 호출 실패: ${e.message}")
        value = null
    }
    return value
}

//
//                                            Upload (POST)
//

// 일주일 메뉴 모두 업로드 하기
suspend fun uploadingWeekMealPlans(
    context: Context,
    restaurantName: String,
    body: requestDTO_week
): AdminResponse? {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Kotlin 지원
        .build()
    val jsonAdapter = moshi.adapter(requestDTO_week::class.java)
    val jsonData = jsonAdapter.toJson(body) // data를 JSON으로 변환

    Log.e("uploadingWeekMealPlans", "jsonData: ${jsonData}")


    var code: Int? = null

    try {
        val response =
            Retrofit_Admin.createApiService(context).setWeekMealPlans(restaurantName, jsonData)
        Log.e("uploadingWeekMealPlans", "응답 데이터: ${response.message()}")
        code = response.code()
        return response.body()
    } catch (e: Exception) {
        Log.e("uploadingWeekMealPlans", "API 호출 실패: $code")
        Toast.makeText(context, "전송 실패: 에러 코드 $code", Toast.LENGTH_SHORT).show()
        return null
    }
}


// 특정 요일 메뉴 업로드하기
suspend fun uploadingMealPlans(
    context: Context,
    restaurantName: String,
    dayOfWeek: String,
    body: requestDTO_dayOfWeek
): AdminResponse? {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Kotlin 지원
        .build()
    val jsonAdapter = moshi.adapter(requestDTO_dayOfWeek::class.java)
    val jsonData = jsonAdapter.toJson(body) // data를 JSON으로 변환

    Log.e("uploadingMealPlans", "jsonData=\n$jsonData")
        var code: Int? = null
    try {
        val response =
            Retrofit_Admin.createApiService(context).setMealPlans(restaurantName, dayOfWeek, jsonData)
        Log.e("uploadingMealPlans", "응답 데이터: ${response.message()}")
        code = response.code()
        return response.body()
    } catch (e: Exception) {
        Log.e("uploadingMealPlans", "API 호출 실패: $code")
        Toast.makeText(context, "전송 실패: 에러 코드 $code", Toast.LENGTH_SHORT).show()
        return null
    }
}