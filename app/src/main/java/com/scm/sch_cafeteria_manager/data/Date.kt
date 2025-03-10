package com.scm.sch_cafeteria_manager.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// navigation args 타입
@Parcelize
data class manageDate(
    val week: String,  // 요일
    val day: String    // 일주일 시작 날짜
): Parcelable
