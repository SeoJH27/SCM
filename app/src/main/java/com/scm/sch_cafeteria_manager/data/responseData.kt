package com.scm.sch_cafeteria_manager.data

import android.graphics.Bitmap
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

data class AdminData(
    val status: String,
    val message: String,
    val data: data
)

//sealed class MealData{
//    data class data(
//        val weekMealImg: String,         // 주간 식단 이미지 경로
//        val dailyMeals: DailyMeals?    // 일일 식단 정보
//    ): MealData()
//}

data class data(
    val weekMealImg: String,         // 주간 식단 이미지 경로
    val dailyMeal: dailyMeal    // 일일 식단 정보
)
//
//sealed class DailyMeals{
//    data class data(
//        val dayOfWeek: String,            // 요일 (예: "MONDAY")
//        val meals: List<Meals>             // 식사 리스트
//    ):DailyMeals()
//}

data class dailyMeal(
    val dayOfWeek: String,
    val meals: List<meals>
)
//
//data class MasterData(
//    val status: String,
//    val message: String,
//    val data: MealWithImg
//)

//data class MealWithImg(
//    val dayMealImg: Bitmap,
//    val weekMealImg: Bitmap,
//    val dailyMeal: DailyMeals
//)