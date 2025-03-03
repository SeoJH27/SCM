package com.scm.sch_cafeteria_manager.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class requestDTO_week(
    val restaurantName: String,
    val weekStartDate: String
) : Parcelable


@Parcelize
data class requestDTO_day(
    val restaurantName: String,
    val dayOfWeek: String,
    val weekStartDate: String
) : Parcelable