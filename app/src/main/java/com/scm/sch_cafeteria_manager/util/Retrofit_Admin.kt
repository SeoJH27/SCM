package com.scm.sch_cafeteria_manager.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.scm.sch_cafeteria_manager.data.AdminResponse
import com.scm.sch_cafeteria_manager.data.WeekAdminResponse
import com.scm.sch_cafeteria_manager.data.dailyMeals
import com.scm.sch_cafeteria_manager.data.requestDTO_dayOfWeek
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
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
import java.io.File
import java.util.concurrent.TimeUnit

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
    ): Response<WeekAdminResponse>

    @Multipart
    @POST("/api/admin/meal-plans/{restaurant-name}/{day-of-week}")
    suspend fun setMealPlans(
        @Path("restaurant-name") resName: String,
        @Path("day-of-week") dayOfWeek: String,
        @Part("weekStartDate") weekStartDate: RequestBody,
        @Part("dailyMeals") dailyMeals: RequestBody,
        @Part dailyMealImg: MultipartBody.Part
    ): Response<AdminResponse>
}

object Retrofit_Admin {
    suspend fun createApiService(context: Context): ApiService_Admin {
        // 만료 확인
        if (isJwtExpired(context)) {
            val re = reissueToAdmin(context)
            Log.e("Retrofit_Admin", "createApiService - reissue: $re")
        }

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor(context))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC // URL, status 만 출력
            })
            .connectTimeout(150, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS).build()

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).client(okHttpClient).build()
            .create(ApiService_Admin::class.java)
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
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()) // Kotlin 지원
        .build()
    // weekly
    val type = Types.newParameterizedType(List::class.java, dailyMeals::class.java)
    val jsonAdapter = moshi.adapter<List<dailyMeals>>(type)
    val jsonData = jsonAdapter.toJson(dM)
    val meal = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

    // start date
    val startDate = weekStartDate.toRequestBody("application/json".toMediaTypeOrNull())

    // image
    val fileImage = weeklyMealImg.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val multiFile = MultipartBody.Part.createFormData("weeklyMealImg", "photo", fileImage)

    Log.e(
        "uploadingWeekMealPlans",
        "List<dailyMeals>: $dM" + "\nweekStartDate: $weekStartDate" + "\nfileImage: ${fileImage.contentLength()}"
    )
    try {
        val response = Retrofit_Admin.createApiService(context)
            .setWeekMealPlans(restaurantName, startDate, meal, multiFile)
        if (response.isSuccessful) {
            Log.e("uploadingWeekMealPlans", "Successful: ${response}")
//            Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show()
        } else {
            Log.e("uploadingWeekMealPlans", " Error: $response")
            Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Log.e("uploadingWeekMealPlans", "API 호출 실패: $e")
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}


// 특정 요일 메뉴 업로드하기
suspend fun uploadingMealPlans(
    context: Context,
    restaurantName: String,
    dayOfWeek: String,
    weekStartDate: String,
    dM: dailyMeals,
    dailyMealImg: File
) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()) // Kotlin 지원
        .build()

    // daily
    val jsonAdapter = moshi.adapter(dailyMeals::class.java)
    val jsonData = jsonAdapter.toJson(dM) // data 를 JSON 으로 변환
    val meal = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

    // start date
    val startDate = weekStartDate.toRequestBody("application/json".toMediaTypeOrNull())

    // image
    val fileImage = dailyMealImg.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val multiFile = MultipartBody.Part.createFormData("dailyMealImg", "photo", fileImage)

    Log.e("uploadingMealPlans", "jsonData=\n$jsonData")
    var error: String? = null
    try {
        val response = Retrofit_Admin.createApiService(context)
            .setMealPlans(restaurantName, dayOfWeek, startDate, meal, multiFile)
        if (response.isSuccessful) {
            Log.e("uploadingMealPlans", "Successful")
            Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show()
        } else {
            error = response.code().toString()
            Log.e("uploadingMealPlans", "Error: $response")
            Toast.makeText(context, "Error Code: ${response.code()}", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Log.e("uploadingMealPlans", "API 호출 실패: $error")
        Toast.makeText(context, "전송 실패: 에러 코드 $error", Toast.LENGTH_SHORT).show()
    }
}
