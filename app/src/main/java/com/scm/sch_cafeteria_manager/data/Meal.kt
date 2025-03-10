package com.scm.sch_cafeteria_manager.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class meals(
    val mealType: String?,
    val operatingStartTime: String?,
    val operatingEndTime: String?,
    val mainMenu: String?,
    val subMenu: String?
): Parcelable

@Parcelize
data class dailyMeals(
    val dayOfWeek: String,
    val meals: List<meals?>
): Parcelable

@Parcelize
data class DetailMenu(
    val restaurantOperatingStartTime: String,
    val restaurantOperatingEndTime: String,
    val dailyMeal: List<dailyMeal>,
    val active: Boolean
): Parcelable
