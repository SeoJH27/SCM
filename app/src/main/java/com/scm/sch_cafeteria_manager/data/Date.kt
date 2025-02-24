package com.scm.sch_cafeteria_manager.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class manageDate(
    val week: String,  // 요일
    val day: String    // 날짜
): Parcelable
