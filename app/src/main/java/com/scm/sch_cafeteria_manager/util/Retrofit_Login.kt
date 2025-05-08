package com.scm.sch_cafeteria_manager.util

import android.content.Context
import android.util.Log
import android.util.Base64
import org.json.JSONObject
import android.widget.Toast
import com.scm.sch_cafeteria_manager.util.utilAll.BASE_URL
import com.scm.sch_cafeteria_manager.data.loginRequest
import com.scm.sch_cafeteria_manager.data.loginResponse
import com.scm.sch_cafeteria_manager.data.logoutResponse
import com.scm.sch_cafeteria_manager.util.PrefHelper_Login.saveTokens
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.net.ConnectException
import java.net.CookieManager
import java.util.concurrent.TimeUnit

interface ApiService_login {
    @Multipart
    @POST("/login")
    suspend fun login(
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody
    ): Response<loginResponse>

    @GET("/logout")
    suspend fun logout(
        @Header("Cookie") cookie: String
    ): Response<logoutResponse>

    @POST("/reissue")
    suspend fun reissue(
        @Header("Cookie") cookie: String
    ): Response<logoutResponse>
}

object Retrofit_Login {
    fun createApiServiceLogin(): ApiService_login {
        val okHttpsClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpsClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService_login::class.java)
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(context))
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()
    }

    fun createApiService(context: Context): ApiService_login {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService_login::class.java)
    }
}

// Headers에 AuthInterceptor 추가
class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = PrefHelper_Login.getAccessToken(context) // SharedPreferences에서 토큰 불러오기
        Log.e("AuthInterceptor", "AccessToken: $token")
        val request = chain.request().newBuilder()
        token?.let {
            request.addHeader("Authorization", "Bearer $it") // Authorization 헤더 추가
        }
        return chain.proceed(request.build())
    }
}

suspend fun loginToAdmin(context: Context, login: loginRequest): Boolean {
    val usernameBody = login.username.toRequestBody("text/plain".toMediaTypeOrNull())
    val passwordBody = login.password.toRequestBody("text/plain".toMediaTypeOrNull())

    Log.e("loginToadmin", "jsonData=\n$login")
    val response = Retrofit_Login.createApiServiceLogin().login(usernameBody, passwordBody)

    Log.e("loginToAdmin", "Response Code: ${response.code()}")
    if (response.isSuccessful) {
        try {
            val statusCode = response.code()
            Log.e("loginToAdmin", "Response Code: $statusCode")
            when (statusCode) {
                200 -> {
                    val headers = response.headers()
                    val accessTk = headers["Authorization"]?.replace("Bearer ", "")
                    val refreshTk = extraRefreshToken(headers)

                    val authority = response.body()!!.data
                    Log.e("loginToAdmin", "Response authority: $authority")

                    // 로그인 성공
                    if (!accessTk.isNullOrEmpty() && !refreshTk.isNullOrEmpty()) {
                        saveTokens(context, accessTk, refreshTk, authority)
                        Toast.makeText(
                            context,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "loginToAdmin",
                            "loginToAdmin:\naccessToken=${PrefHelper_Login.getAccessToken(context)}\nrefreshToken=${
                                PrefHelper_Login.getRefreshToken(context)
                            }"
                        )

                        return true

                    } else {
                        //TODO: 사용자 입장에서 처리 간편하게
                        Toast.makeText(
                            context,
                            "Tokens missing in response",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                401 -> {
                    val errorResponse = response.body()?.message ?: "아이디 혹은 비밀번호가 일치하지 않습니다."
                    Toast.makeText(context, errorResponse, Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(
                        context,
                        "Unexpected Error: $statusCode",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: ConnectException) {
            Toast.makeText(context, "네트워크가 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("loginToAdmin", "Exception: ${e.message}")
        }
    } else {
        Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
        Log.e("loginToAdmin", "Successful Error: ${response.message()}")
    }
    return false
}

suspend fun logoutToAdmin(context: Context) {
    try {
        val refreshTk = PrefHelper_Login.getRefreshToken(context)
        if (refreshTk.isNullOrEmpty()) {
            throw Exception("refresh token 없음")
        } else {
            val response = Retrofit_Login.createApiService(context).logout("refresh=$refreshTk")
            PrefHelper_Login.deleteTokens(context)
            if (response.isSuccessful) {
                try {
                } catch (e: ConnectException) {
                    Toast.makeText(context, "네트워크가 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    //Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("logoutToAdmin", "Successful Error: ${response.message()}")
                }
            } else {
                //Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                Log.e("logoutToAdmin", "Error: ${response}")
            }
        }
    } catch (e: Exception) {
        //Toast.makeText(context, "Error: $e", Toast.LENGTH_SHORT).show()
        Log.e("logoutToAdmin", "Error: $e")
    }
}

suspend fun reissueToAdmin(context: Context): Boolean {
    Log.e(
        "reissue",
        "reissue:\naccessToken=${PrefHelper_Login.getAccessToken(context)}\nrefreshToken=${
            PrefHelper_Login.getRefreshToken(context)
        }"
    )

    try {
        val refreshTk = PrefHelper_Login.getRefreshToken(context)
        if (refreshTk.isNullOrEmpty()) {
            throw NullPointerException("refresh token 없습니다.")
        } else {
            val refresh = "refresh=${refreshTk}"
            val response = Retrofit_Login.createApiServiceLogin().reissue(refresh)
            if (response.isSuccessful) {
                when (response.code()) {
                    200 -> {
                        val headers = response.headers()
                        val accessTk = headers["Authorization"]?.replace("Bearer ", "")!!
                        val newRefreshTk = extraRefreshToken(headers)
                        if (accessTk.isNotEmpty() && newRefreshTk != null) {
                            PrefHelper_Login.resetToken(context, accessTk, newRefreshTk)
                            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show()
                            Log.e("reissue", "reissue: accessTk = $newRefreshTk")
                            return true
                        } else return false
                    }

                    else -> {
                        Toast.makeText(
                            context,
                            "Error: ${response.errorBody()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("reissue", "${response.code()} Error: ${response.errorBody()}")
                    }
                }
            } else {
                Log.e("reissue", "reissue: refreshTk = $refreshTk")
                Toast.makeText(context, "로그인이 만료되었습니다.\n다시 로그인해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    } catch (e: ConnectException) {
        Toast.makeText(context, "네트워크가 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error: $e", Toast.LENGTH_SHORT).show()
        Log.e("reissue", "Error: $e")
    }
    return false
}

// Refresh Token 가져오기
private fun extraRefreshToken(headers: okhttp3.Headers): String? {
    val cookies = headers["Set-Cookie"]
    val cookieList = headers.values("Set-Cookie")

    if (cookies != null) {
        Log.e("loginToAdmin", "Cookies: Single Cookie: $cookies")
    }
    if (cookieList.isNotEmpty()) {
        for (cookie in cookieList) {
            if (cookie.startsWith("refresh=")) {
                return cookie.substringAfter("refresh=").substringBefore(";")
            }
        }
    }
    return null
}

// 만료 여부 체크: 디코딩 하여 만료 시간 파악 (true: 만료, false: 유효)
fun isJwtExpired(context: Context): Boolean {
    val token = PrefHelper_Login.getAccessToken(context)
    if (token != null) {
        try {
            val parts = token.split(".")
            if (parts.size != 3) return true

            val payload = String(
                Base64.decode(
                    parts[1],
                    Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )
            )
            val json = JSONObject(payload)

            val exp = json.optLong("exp", 0L)
            if (exp == 0L) return true

            val currentTime = System.currentTimeMillis() / 1000 // 현재 시간 (초 단위)
            return currentTime >= exp
        } catch (e: Exception) {
            return true // 파싱 실패 시 만료된 것으로 간주
        }
    } else return true
}