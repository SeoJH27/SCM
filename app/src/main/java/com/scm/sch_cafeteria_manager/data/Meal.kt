package com.scm.sch_cafeteria_manager.data
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

// fetch
@Parcelize
@JsonClass(generateAdapter = true)
data class meals(
    @Json(name = "mealType") val mealType: String,
    @Json(name = "operatingStartTime") val operatingStartTime: String,
    @Json(name = "operatingEndTime") val operatingEndTime: String,
    @Json(name = "mainMenu") val mainMenu: String,
    @Json(name = "subMenu") val subMenu: String
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class dailyMeals(
    @Json(name = "dayOfWeek") val dayOfWeek: String?,
    @Json(name = "meals") val meals: List<meals?>
): Parcelable

// detail
@Parcelize
@JsonClass(generateAdapter = true)
data class dataDetail(
    @Json(name = "restaurantOperatingStartTime") val restaurantOperatingStartTime: String,
    @Json(name = "restaurantOperatingEndTime") val restaurantOperatingEndTime: String,
    @Json(name = "dailyMeals") val dailyMeals: List<dailyMeals>,
    @Json(name = "active") val active: Boolean
): Parcelable
