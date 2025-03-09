package com.scm.sch_cafeteria_manager.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meal(
    val mealType: String?,
    val operatingStartTime: String?,
    val operatingEndTime: String?,
    val mainMenu: String?,
    val subMenu: String?
): Parcelable

data class meals(
    val mealType: String,
    val operatingStartTime: String,
    val operatingEndTime: String,
    val mainMenu: String,
    val subMenu: String
)

//sealed class Meals{
//    data class data(
//        val mealType: String?,
//        val operatingStartTime: String?,
//        val operatingEndTime: String?,
//        val mainMenu: String?,
//        val subMenu: String?
//    ): Meals()
//}
