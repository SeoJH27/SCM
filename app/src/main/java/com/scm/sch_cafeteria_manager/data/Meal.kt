package com.scm.sch_cafeteria_manager.data

data class Meal(
    val mealType: String,
    val operatingStartTime: String,
    val operatingEndTime: String,
    val mainMenu: String,
    val subMenu: String
)
