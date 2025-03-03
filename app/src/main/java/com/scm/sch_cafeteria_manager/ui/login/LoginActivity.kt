package com.scm.sch_cafeteria_manager.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.data.loginRequest
import com.scm.sch_cafeteria_manager.data.loginResponse
import com.scm.sch_cafeteria_manager.databinding.ActivityLoginBinding
import com.scm.sch_cafeteria_manager.ui.admin.AdminActivity
import com.scm.sch_cafeteria_manager.ui.home.HomeActivity
import com.scm.sch_cafeteria_manager.util.PrefHelper_Login.saveTokens
import com.scm.sch_cafeteria_manager.util.Retrofit_Login
import com.scm.sch_cafeteria_manager.util.cacheHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body


//TODO: TEST!!!!!
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            if (isAdminLoggedIn()) {
                startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                finish()
            }
            btnLoginConfirm.setOnClickListener {

                //화면 테스트
                cacheHelper.saveToCache(this@LoginActivity, "authority", "3")
                startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                finish()

//                val id = editLoginId.text.toString()
//                val password = editLoginPassword.text.toString()
//
//                //TODO: 서버로 보내서 인증 -> Test 필요
//                if (id.isEmpty() && password.isEmpty()) {
//                    Toast.makeText(this@LoginActivity, "아이디 혹은 비밀번호를 입력하세요.", Toast.LENGTH_LONG)
//                        .show()
//                } else if (id.length > 4 && password.length < 4) {
//                    Toast.makeText(
//                        this@LoginActivity,
//                        "아이디 혹은 비밀번호가 4자리 이상이어야 합니다.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                } else {
//                    login(id, password)
//
//                }
            }

            // 뒤로가기
            appbarLogin.setNavigationOnClickListener {
                backDialog()
            }
        }
    }

    // 자동 로그인 체크
    private fun isAdminLoggedIn(): Boolean {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        Log.e("LoginActivity", "isAdminLoggedIn prefs: $prefs")

        return prefs.getString("isLoggedIn", null) != null
    }

    private fun login(id: String, password: String) {
        val request = loginRequest(id, password)

        Retrofit_Login.getInstance(this@LoginActivity).login(request)
            .enqueue(object : Callback<loginResponse> {
                override fun onResponse(
                    call: Call<loginResponse>,
                    response: Response<loginResponse>
                ) {
                    val statusCode = response.code()
                    Log.e("LoginActivity", "Response Code: $statusCode")

                    if (statusCode == 200) {
                        val headers = response.headers()
                        val accessTk = headers["Authorization"]?.replace("Bearer ", "")
                        val refreshTk = extraRefreshToken(headers)
                        val authority = response.message()

                        if (!accessTk.isNullOrEmpty() && !refreshTk.isNullOrEmpty()) {
                            saveTokens(this@LoginActivity, accessTk, refreshTk)
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                            finish()
                        } else {
                            //TODO: 사용자 입장에서 처리 간편하게
                            Toast.makeText(
                                this@LoginActivity,
                                "Tokens missing in response",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else if (statusCode == 401) {
                        val errorResponse = response.body()?.error ?: "Unauthorized access"
                        Toast.makeText(this@LoginActivity, errorResponse, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Unexpected Error: $statusCode",
                            Toast.LENGTH_LONG
                        ).show()
                    }


//                if (response.isSuccessful) {
//                    //TODO: 응답을 받아서
//                    val logResponse = response.headers()
//                    saveLoginState(logResponse)
//                    Log.e("LoginActivity", "Login Successful")
//                    startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
//                    finish()
//                } else {
//                    try {
//                        val errorBody = response.errorBody()?.toString()
//                        Log.e("LoginActivity", "Error Body: $errorBody")
//                        Toast.makeText(this@LoginActivity, "Error: $errorBody", Toast.LENGTH_SHORT)
//                            .show()
//                    } catch (e: Exception) {
//                        Log.e("LoginActivity", "API_ERROR: Error parsing response", e)
//                    }
//                }
                }

                override fun onFailure(call: Call<loginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Network Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("LoginActivity", "Network Error")
                }

            })
    }

    private fun extraRefreshToken(headers: okhttp3.Headers): String? {
        val cookies = headers["Set-Cookie"]
        val cookieList = headers.values("Set-Cookie")

        if (cookies != null) {
            Log.e("LoginActivity", "Cookies: Single Cookie: $cookies")
        }
        if (cookieList.isNotEmpty()) {
            for (cookie in cookieList) {
                if (cookie.startsWith("refreshToken=")) {
                    return cookie.substringAfter("refreshToken=").substringBefore(";")
                }
            }
        }
        return null
    }

    // 뒤로가기 전 알림
    private fun backDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage("뒤로가기 시 로그아웃 됩니다.\n홈 화면으로 돌아가시겠습니까?")
            .setNegativeButton("취소") { dialog, which ->
                // 취소 시 아무 액션 없음
            }
            .setPositiveButton("확인") { dialog, which ->
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }
            .show()
    }
}