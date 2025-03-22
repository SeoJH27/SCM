package com.scm.sch_cafeteria_manager.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.scm.sch_cafeteria_manager.data.AdminResponse
import com.scm.sch_cafeteria_manager.data.WeekAdminResponse
import com.scm.sch_cafeteria_manager.data.dailyMeals
import com.scm.sch_cafeteria_manager.data.dataAdmin
import com.scm.sch_cafeteria_manager.data.requestDTO_dayOfWeek
import com.scm.sch_cafeteria_manager.data.weekAdmin
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.Response as okResponse


interface ApiService_Admin {
    @Headers("Content-Type: application/json")
    @GET("/api/admin/meal-plans")
    suspend fun getMealPlans(@QueryMap pendingDailyMealRequestDTO: Map<String, String>): Response<AdminResponse>

    @Multipart
    @POST("/api/admin/week-meal-plans/{restaurant-name}")
    suspend fun setWeekMealPlans(
        @Path("restaurant-name") resName: String,
        @Part("weekStartDate") weekStartDate: RequestBody,
        @Part("dailyMeals") dailyMeals: RequestBody,
        @Part weeklyMealImg: MultipartBody.Part
    ): WeekAdminResponse?

    @Headers("Content-Type: application/json")
    @POST("/api/admin/meal-plans/{restaurant-name}/{day-of-week}")
    suspend fun setMealPlans(
        @Path("restaurant-name") resName: String,
        @Path("day-of-week") dayOfWeek: String,
        @Body requestDTO_dayOfWeek: String
    ): Call<AdminResponse>
}

object Retrofit_Admin {
    fun createApiService(context: Context): ApiService_Admin {
        // Headers에 AuthInterceptor 추가
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor_Admin(context))
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
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
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
        } else {
            value = null
        }
        Log.e("fetchMealPlans", "응답 데이터: ${value?.data}")
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
    weekStartDate: String,
    dM: List<dailyMeals>,
    weeklyMealImg: File,
) {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Kotlin 지원
        .build()
    // daily
    val type = Types.newParameterizedType(List::class.java, dailyMeals::class.java)
    val jsonAdapter = moshi.adapter<List<dailyMeals>>(type)
    val jsonData = jsonAdapter.toJson(dM)
    val meal = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

    // start date
    val startDate = weekStartDate.toRequestBody("application/json".toMediaTypeOrNull())

    // image
    val fileImage = weeklyMealImg.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val multiFile = MultipartBody.Part.createFormData("weeklyMealImg", "photo", fileImage)

    Log.e("uploadingWeekMealPlans", "List<dailyMeals>: $meal"
                                         +"\nweekStartDate: $startDate"
                                         +"\nfileImage: $fileImage")

    var response: WeekAdminResponse? = null
    try {
        response =
            Retrofit_Admin.createApiService(context)
                .setWeekMealPlans(restaurantName, startDate, meal, multiFile)
        Log.e("uploadingWeekMealPlans", "응답 데이터: ${response?.status}")
    } catch (e: Exception) {
        Log.e("uploadingWeekMealPlans", "API 호출 실패: $e")
    }
}


// 특정 요일 메뉴 업로드하기
suspend fun uploadingMealPlans(
    context: Context,
    restaurantName: String,
    dayOfWeek: String,
    body: requestDTO_dayOfWeek
) {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Kotlin 지원
        .build()
    val jsonAdapter = moshi.adapter(requestDTO_dayOfWeek::class.java)
    val jsonData = jsonAdapter.toJson(body) // data를 JSON으로 변환

    Log.e("uploadingMealPlans", "jsonData=\n$jsonData")
    var error: String? = null
    try {
        val response =
            Retrofit_Admin.createApiService(context)
                .setMealPlans(restaurantName, dayOfWeek, jsonData)
        Log.e("uploadingMealPlans", "응답 데이터: ${response.request()}")
        error = response.timeout().toString()
    } catch (e: Exception) {
        Log.e("uploadingMealPlans", "API 호출 실패: $error")
        Toast.makeText(context, "전송 실패: 에러 코드 $error", Toast.LENGTH_SHORT).show()
    }
}