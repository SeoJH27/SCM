package com.scm.sch_cafeteria_manager.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class requestDTO_week(
    val restaurantName: String,
    val weekStartDate: String
) : Parcelable

@Parcelize
data class requestDTO_dayOfWeek(
    val weekStartDate: String,
    val dailyMeals: Daily,
    val dailyMealImg: String
) : Parcelable

@Parcelize
data class requestDTO_week_master(
    val weekStartDate: String,
    val dailyMeals: Daily,
) : Parcelable


@Parcelize
data class pendingDailyMealRequestDTO(
    val restaurantName: String,
    val dayOfWeek: String,
    val weekStartDate: String
) : Parcelable