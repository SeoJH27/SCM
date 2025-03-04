package com.scm.sch_cafeteria_manager.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailMenu(
    val restaurantOperatingStartTime: String,
    val restaurantOperatingEndTime: String,
    val dailyMeals: List<Daily>,
    val active: Boolean
): Parcelable
