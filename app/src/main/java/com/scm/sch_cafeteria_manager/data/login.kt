package com.scm.sch_cafeteria_manager.data

const val BASE_URL = "http://3.36.206.211:8080"

data class loginRequest(
    val loginId: String,
    val password: String
)

data class loginResponse(
    val success: Boolean, // 요청 성공 여부
    val message: String?, // 권한
    val error: String?   // 401 에러 메세지 저장
)
