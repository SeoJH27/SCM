// TODO: Moshi Test 이후 다시 돌려놓기
//package com.scm.sch_cafeteria_manager.util
//
//import android.content.Context
//import android.util.Log
//import com.google.gson.Gson
//import com.scm.sch_cafeteria_manager.data.AdminData
//import com.scm.sch_cafeteria_manager.data.MasterData
//import com.scm.sch_cafeteria_manager.data.adminResponse
//import com.scm.sch_cafeteria_manager.data.requestDTO_week_master
//import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
//import okhttp3.Interceptor
//import okhttp3.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.Body
//import retrofit2.http.Headers
//import retrofit2.http.POST
//import retrofit2.http.Path
//import retrofit2.http.QueryMap
//
//interface ApiService_Master {
//    @Headers("Content-Type: application/json")
//    @POST("/api/master/meal-plans")
//    suspend fun getMealPlans(@QueryMap pendingDailyMealRequestDTO: Map<String, String>): MasterData
//
//    @Headers("Content-Type: application/json")
//    @POST("/api/master/week-meal-plans")
//    suspend fun getWeekMealPlans(@QueryMap pendingWeeklyMealRequestDTO : Map<String, String>): MasterData
//
//    @Headers("Content-Type: application/json")
//    @POST("/api/admin/meal-plans/{restaurant-name}")
//    suspend fun setMealPlansMaster(@Path("restaurant-name") resName: String, @Body requestDTO_week_master: String): adminResponse
//}
//
//object Retrofit_Master {
//    fun createApiService(context: Context): ApiService_Master {
//        // Headers에 AuthInterceptor 추가
////        val okHttpClient = OkHttpClient.Builder()
////            .addInterceptor(AuthInterceptor_Master(context))
////            .build()
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            //.client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService_Master::class.java)
//    }
//}
//
//private class AuthInterceptor_Master(private val context: Context) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val token = PrefHelper_Login.getAccessToken(context) // SharedPreferences에서 토큰 불러오기
//        val request = chain.request().newBuilder()
//
//        token?.let {
//            request.addHeader("Authorization", "Bearer $it") // Authorization 헤더 추가
//        }
//        return chain.proceed(request.build())
//    }
//}
//
//// 일주일 메뉴 불러오기
//suspend fun fetchWeekMealPlansMaster(
//    context: Context,
//    weekStartDate: String,
//    restaurantName: String
//): MasterData? {
//    val requestDTO = mapOf(
//        "weekStartDate" to weekStartDate,
//        "restaurantName" to restaurantName
//    )
//
//    Log.e("fetchWeekMealPlansMaster", "requestDTO: $requestDTO")
//
//    try {
//        val response = Retrofit_Master.createApiService(context).getWeekMealPlans(requestDTO)
//        Log.e("fetchWeekMealPlansMaster", "응답 데이터: $response")
//        return response
//    } catch (e: Exception) {
//        Log.e("fetchWeekMealPlansMaster", "API 호출 실패: ${e.message}")
//        return null
//    }
//}
//
//// 특정 요일 메뉴 불러오기
//suspend fun fetchMealPlansMaster(
//    context: Context,
//    weekStartDate: String,
//    dayOfWeek: String,
//    restaurantName: String
//): MasterData? {
//    val requestDTO = mapOf(
//        "weekStartDate" to weekStartDate,
//        "dayOfWeek" to dayOfWeek,
//        "restaurantName" to restaurantName
//    )
//
//    Log.e("fetchMealPlansMaster", "requestDTO: $requestDTO")
//
//    try {
//        val response = Retrofit_Master.createApiService(context).getMealPlans(requestDTO)
//        Log.e("fetchMealPlansMaster", "응답 데이터: $response")
//        return response
//    } catch (e: Exception) {
//        Log.e("fetchMealPlansMaster", "API 호출 실패: ${e.message}")
//        return null
//    }
//}
//
//
//// 일주일 메뉴 불러오기
//suspend fun uploadingWeekMealPlansMaster(
//    context: Context,
//    restaurantName: String,
//    body: requestDTO_week_master
//): adminResponse? {
//    val jsonData = Gson().toJson(body) // data를 JSON으로 변환
//
//    Log.e("uploadingWeekMealPlansMaster", "jsonData: $jsonData")
//
//    try {
//        val response = Retrofit_Master.createApiService(context).setMealPlansMaster(restaurantName, jsonData)
//        Log.e("uploadingWeekMealPlansMaster", "응답 데이터: $response")
//        return response
//    } catch (e: Exception) {
//        Log.e("uploadingWeekMealPlansMaster", "API 호출 실패: ${e.message}")
//        return null
//    }
//}