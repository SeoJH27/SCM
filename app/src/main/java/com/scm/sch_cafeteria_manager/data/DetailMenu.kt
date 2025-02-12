package com.scm.sch_cafeteria_manager.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailMenu(
    val restaurantName: String,
    val weekStartDate: String,
    val operatingStartTime: String,
    val operatingEndTime: String,
    val isActive: Boolean,
    val dailyMealPlans: Week,
): Parcelable
