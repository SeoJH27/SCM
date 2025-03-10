package com.scm.sch_cafeteria_manager.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodayMenu(
    val weekStartDate: String,
    val restaurants: List<Cafeteria>
): Parcelable

@Parcelize
data class Cafeteria(
    val restaurantName: String,
    val meals: List<meals?>
): Parcelable
