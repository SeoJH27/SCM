package com.scm.sch_cafeteria_manager.util

import android.content.Context
import com.scm.sch_cafeteria_manager.data.BASE_URL
import com.scm.sch_cafeteria_manager.data.loginRequest
import com.scm.sch_cafeteria_manager.data.loginResponse
import com.scm.sch_cafeteria_manager.ui.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService_login {
    @Headers("Content-Type: application/json")
    @POST("/reissue") // 로그인 API 엔드 포인트 // TODO: 설정 안함
    fun login(@Body request: loginRequest) : Call<loginResponse>
}

object Retrofit_Login {

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor_Login(context)) //Authorization 헤더 자동 추가
            .build()
    }
    fun getInstance(context: LoginActivity):ApiService_login{
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

private class AuthInterceptor_Login(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = PrefHelper_Login.getAccessToken(context)
        val request= chain.request().newBuilder()

        if(!accessToken.isNullOrEmpty()){
            request.addHeader("Authorization", "Bearer $accessToken")
        }
        return chain.proceed(request.build())
    }
}