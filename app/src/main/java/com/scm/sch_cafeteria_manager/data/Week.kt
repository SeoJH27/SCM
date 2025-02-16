package com.scm.sch_cafeteria_manager.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Week(
    val dailyMealPlans: List<Daily>
): Parcelable


@Parcelize
data class Daily(
    val dayOfWeek: String,
    val meals: List<Meal>
): Parcelable