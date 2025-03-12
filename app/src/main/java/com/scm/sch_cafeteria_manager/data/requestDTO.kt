package com.scm.sch_cafeteria_manager.data

import android.graphics.Bitmap
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class requestDTO_week(
    @Json(name = "weekStartDate") val weekStartDate: String,
    @Json(name = "dailyMeals") val dailyMeals: List<dailyMeals>,
    @Json(name = "weeklyMealImg") val weeklyMealImg: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class requestDTO_dayOfWeek(
    @Json(name = "weekStartDate") val weekStartDate: String,
    @Json(name = "dailyMeals") val dailyMeals: dailyMeals,
    @Json(name = "dailyMealImg") val dailyMealImg: String
) : Parcelable

@Parcelize
data class requestDTO_week_master(
    val weekStartDate: String,
    val dailyMeals: dailyMeals,
) : Parcelable


@Parcelize
data class pendingDailyMealRequestDTO(
    val restaurantName: String,
    val dayOfWeek: String,
    val weekStartDate: String
) : Parcelable