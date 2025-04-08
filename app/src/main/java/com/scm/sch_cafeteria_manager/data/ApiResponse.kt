package com.scm.sch_cafeteria_manager.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

// 디테일 API Response
@JsonClass(generateAdapter = true)
data class UserDetailResponse(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: dataDetail
)

// 오늘의 메뉴 API Response
data class UserTodayMenuResponse(
    val status: String,
    val message: String,
    val data: List<todayRes>
)

// fetch시 사용하는 AdminResponse
@Parcelize
data class AdminResponse(
    val status: String,
    val message: String,
    val data: dataAdmin
): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class WeekAdminResponse(
    val status: String,
    val message: String,
    val data: weekAdmin
): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class weekAdmin(
//    @Json(name = "weekStartDate") val weekStartDate: String?,
    @Json(name = "weekMealImg") val weekMealImg: String,         // 주간 식단 이미지 경로
    @Json(name = "dailyMeals") val dailyMeals: List<dailyMeals>?    // 일일 식단 정보
): Parcelable

@Parcelize
data class dataAdmin(
    @Json(name = "weekMealImg") val weekMealImg: String,         // 주간 식단 이미지 경로
    @Json(name = "dailyMeal") val dailyMeal: dailyMeal    // 일일 식단 정보
): Parcelable

@Parcelize
data class dailyMeal(
    @Json(name = "dayOfWeek") val dayOfWeek: String,
    @Json(name = "meals") val meals: List<meals>?
): Parcelable


// fetch시 사용하는 MasterResponse
@Parcelize
data class MasterResponse(
    val status: String,
    val message: String,
    val data: dataMaster
): Parcelable

@Parcelize
data class dataMaster(
    val dayMealImg: String?,
    val weekMealImg: String,
    val dailyMeal: dailyMeal
): Parcelable

// fetch시 사용하는 MasterResponse
@Parcelize
data class MasterWeekResponse(
    val status: String,
    val message: String,
    val data: dataWeekMaster
): Parcelable

@Parcelize
data class dataWeekMaster(
    val dayMealImg: String?,
    val weekMealImg: String,
    @Json(name = "dailyMeals") val dailyMeals: List<dailyMeals>
): Parcelable