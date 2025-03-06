package com.scm.sch_cafeteria_manager.util

import com.scm.sch_cafeteria_manager.data.Meal
import com.scm.sch_cafeteria_manager.data.dOw
import java.util.Calendar

object utilAll {

    val blank = ""
    val nonData = "정보 없음"

    const val BASE_URL = "http://3.36.206.211:8080"

    // TODO : 비었을 때 사용. 더 적절한 용어 필요함.
    val emptyMEAL = listOf(
        Meal(blank, blank, blank, nonData, nonData)
    )

    fun setInquiryLink() {
        // TODO: 카카오톡 오픈채팅방 하이퍼링크
    }

    fun doDayOfWeek(): String {
        val cal: Calendar = Calendar.getInstance()
        val nWeek: Int = cal.get(Calendar.DAY_OF_WEEK)

        if (nWeek == 1) {
            return dOw.MONDAY.dName
        } else if (nWeek == 2) {
            return dOw.MONDAY.dName
        } else if (nWeek == 3) {
            return dOw.TUESDAY.dName
        } else if (nWeek == 4) {
            return dOw.THURSDAY.dName
        } else if (nWeek == 5) {
            return dOw.THURSDAY.dName
        } else if (nWeek == 6) {
            return dOw.FRIDAY.dName
        } else if (nWeek == 7) {
            return dOw.FRIDAY.dName
        }else{
            return blank
        }
    }

    fun weekToInt(){

    }
}