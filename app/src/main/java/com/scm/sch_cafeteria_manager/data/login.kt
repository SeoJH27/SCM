package com.scm.sch_cafeteria_manager.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class loginRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
): Parcelable

@Parcelize
data class loginResponse(
    @Json(name = "status") val status: String, // 요청 성공 여부
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: String    // 권한
): Parcelable

@Parcelize
data class logoutResponse(
    @Json(name = "status") val status: String, // 요청 성공 여부
    @Json(name = "message") val message: String
): Parcelable



