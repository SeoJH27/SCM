package com.scm.sch_cafeteria_manager.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


// fetch시 사용하는 AdminData
@Parcelize
data class AdminData(
    val status: String,
    val message: String,
    val data: dataAdmin
): Parcelable
@Parcelize
data class dataAdmin(
    val weekMealImg: String,         // 주간 식단 이미지 경로
    val dailyMeal: dailyMeal    // 일일 식단 정보
): Parcelable
@Parcelize
data class dailyMeal(
    val dayOfWeek: String,
    val meals: List<meals>
): Parcelable


// fetch시 사용하는 MasterData
@Parcelize
data class MasterData(
    val status: String,
    val message: String,
    val data: dataMaster
): Parcelable
@Parcelize
data class dataMaster(
    val dayMealImg: String,
    val weekMealImg: String,
    val dailyMeal: dailyMeal
): Parcelable