package com.scm.sch_cafeteria_manager.util

import android.content.Context
import com.scm.sch_cafeteria_manager.data.loginRequest
import com.scm.sch_cafeteria_manager.data.loginResponse
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import

interface ApiService_login {
    @Headers("Content-Type: application/json")
    @POST("login") // 로그인 API 엔드 포인트
    fun login(@Body request: loginRequest) : Call<loginResponse>
}

object Retrofit_Login {
    private const val BASE_URL = "https:///api/auth/login"

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) //Authorization 헤더 자동 추가
            .build()
    }
    fun getInstance(context: Context):ApiService_login{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService_login::class.java)
    }

//    val instance: ApiService_login by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService_login::class.java)
//    }

}

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = PrefHelper_Login.getAccessToken(context)
        val request= chain.request().newBuilder()

        if(!accessToken.isNullOrEmpty()){
            request.addHeader("Authorization", "Bearer $accessToken")
        }
        return chain.proceed(request.build())
    }
}