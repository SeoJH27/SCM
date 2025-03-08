package com.scm.sch_cafeteria_manager.data

enum class UserRole {
    MASTER, ADMIN1, ADMIN2, ADMIN3
}

enum class CafeteriaData(
    val cfName: String
) {
    HYANGSEOL1("HYANGSEOL1"), FACULTY("FACULTY")
}

enum class dOw(
    val dName: String
) {
    MONDAY("MONDAY"), TUESDAY("TUESDAY"), WEDNESDAY("WEDNESDAY"), THURSDAY("THURSDAY"), FRIDAY("FRIDAY")
}

enum class MealType(
    val myNmae: String
){
    BREAKFAST("BREAKFAST"), LUNCH("LUNCH"), DINNER("DINNER")
}

