package com.scm.sch_cafeteria_manager.data

data class AdminData(
    val status: String,
    val message: String,
    val data: MealData
)

data class MealData(
    val weekMealImg: String,         // 주간 식단 이미지 경로
    val dailyMeals: DailyMeals        // 일일 식단 정보
)

data class DailyMeals(
    val dayOfWeek: String,            // 요일 (예: "MONDAY")
    val meals: List<Meal>             // 식사 리스트
)