package com.scm.sch_cafeteria_manager.data

data class DetailMenu(
    val restaurantName: String,
    val weekStartDate: String,
    val operatingStartTime: String,
    val operatingEndTime: String,
    val isActive: Boolean,
    val dailyMealPlans: Week,
)
