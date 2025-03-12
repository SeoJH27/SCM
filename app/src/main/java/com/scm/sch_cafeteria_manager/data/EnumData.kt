package com.scm.sch_cafeteria_manager.data

enum class UserRole {
    MASTER, ADMIN1, ADMIN2, ADMIN3
}

enum class CafeteriaData(
    val cfName: String,
) {
    HYANGSEOL1("HYANGSEOL1"), FACULTY("FACULTY")
}

enum class dOw(
    val dName: String,
    val korName: String
) {
    MONDAY("MONDAY", "월요일"),
    TUESDAY("TUESDAY", "화요일"),
    WEDNESDAY("WEDNESDAY", "수요일"),
    THURSDAY("THURSDAY", "목요일"),
    FRIDAY("FRIDAY", "금요일"),
    SATURDAY("SATURDAY", "토요일"),
    SUNDAY("SUNDAY", "일요일")
}

enum class MealType(
    val myNmae: String,
    val korName: String

){
    BREAKFAST("BREAKFAST", "조식"),
    LUNCH("LUNCH", "중식"),
    DINNER("DINNER", "석식")
}

