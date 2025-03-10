package com.scm.sch_cafeteria_manager.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.scm.sch_cafeteria_manager.data.dOw
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.extentions.replaceCommaToLinebreak
import kotlinx.coroutines.CoroutineStart
import java.util.Calendar
import java.util.Objects.isNull


object utilAll {

    val blank = ""
    val nonData = "정보 없음"
    val nonDate = "00:00"

    const val BASE_URL = "http://3.36.206.211:8080"
    const val photoFilePath = "photo.jpg"


    // TODO : 비었을 때 사용. 더 적절한 용어 필요함.
    val emptyMEAL = listOf(
        meals(blank, blank, blank, nonData, nonData)
    )

    fun setInquiryLink() {
        // TODO: 카카오톡 오픈채팅방 하이퍼링크
    }

    fun intToDayOfWeek(): String {
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

    fun stringToBitmap(img: String?): Bitmap?{
        try{
            val encodeByte = Base64.decode(img, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception){
            Log.e("utilAll", "stringToBitmap - Error $e")
            return null
        }
    }

    fun combinMainAndSub(mainMenu: String?, subMenu: String?): String?{
        if(isNull(mainMenu))
            return null
        else{
            if(isNull(subMenu)){
                return mainMenu?.replaceCommaToLinebreak()
            } else{
                val menu = mainMenu?.replaceCommaToLinebreak() + "\n" + subMenu?.replaceCommaToLinebreak()
                return menu
            }
        }
    }
}