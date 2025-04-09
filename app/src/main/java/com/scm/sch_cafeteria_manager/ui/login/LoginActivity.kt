package com.scm.sch_cafeteria_manager.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.data.UserRole
import com.scm.sch_cafeteria_manager.data.loginRequest
import com.scm.sch_cafeteria_manager.databinding.ActivityLoginBinding
import com.scm.sch_cafeteria_manager.ui.admin.AdminActivity
import com.scm.sch_cafeteria_manager.ui.home.HomeActivity
import com.scm.sch_cafeteria_manager.util.PrefHelper_Login
import com.scm.sch_cafeteria_manager.util.PrefHelper_Login.getIsLoggedIn
import com.scm.sch_cafeteria_manager.util.loginToAdmin
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            // 로그인 체크
            Log.e("LoginActivity", "isAdminLoggedIn prefs Check")
            if (getIsLoggedIn(this@LoginActivity)) {
                Log.e("LoginActivity", "isAdminLoggedIn prefs Check - true")
                startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                finish()
            }
            btnLoginConfirm.setOnClickListener {
//                test()
                setLogin()
            }
            // 뒤로가기
            setBack()
        }
    }

//    private fun test() {
//        saveTokens(this@LoginActivity, "", "", UserRole.ADMIN1.name)
//        startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
//        finish()
//    }

    private fun setLogin() {
        with(binding) {
            val id = editLoginId.text.toString()
            val password = editLoginPassword.text.toString()

            if (id.isEmpty() && password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "아이디 혹은 비밀번호를 입력하세요.", Toast.LENGTH_LONG)
                    .show()
            } else if (id.length > 4 && password.length < 4) {
                Toast.makeText(
                    this@LoginActivity,
                    "아이디 혹은 비밀번호가 4자리 이상이어야 합니다.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                login(id, password)
            }
        }
    }

    // 로그인
    private fun login(id: String, password: String) {
        val request = loginRequest(id, password)
        binding.progressbar.visibility = View.VISIBLE
        binding.progressbarBackground.visibility = View.VISIBLE
        binding.progressbarBackground.isClickable = true

        lifecycleScope.launch {
            val response = loginToAdmin(this@LoginActivity, request)
            Log.e("LoginActivity", "loginToAdmin: $response")

            if (response) {
                Log.e(
                    "LoginActivity",
                    "accessToken: ${PrefHelper_Login.getAccessToken(this@LoginActivity)}"
                )
                Log.e(
                    "LoginActivity",
                    "refreshToken: ${PrefHelper_Login.getRefreshToken(this@LoginActivity)}"
                )
                startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                finish()
            } else {
                Log.e(
                    "LoginActivity",
                    "accessToken: ${PrefHelper_Login.getAccessToken(this@LoginActivity)}"
                )
                Log.e(
                    "LoginActivity",
                    "refreshToken: ${PrefHelper_Login.getRefreshToken(this@LoginActivity)}"
                )
                Toast.makeText(
                    this@LoginActivity,
                    "로그인 오류",
                    Toast.LENGTH_LONG
                ).show()
            }
            binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
            binding.progressbarBackground.visibility = View.GONE
        }

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

    private fun setBack() {
        binding.appbarLogin.setNavigationOnClickListener {
            backDialog()
        }
    }
}