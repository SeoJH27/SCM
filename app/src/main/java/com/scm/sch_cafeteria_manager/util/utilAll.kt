package com.scm.sch_cafeteria_manager.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.scm.sch_cafeteria_manager.data.MealType
import com.scm.sch_cafeteria_manager.data.dOw
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.extentions.replaceCommaToLinebreak
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Objects.isNull


object utilAll {

    val blank = " "
    val nonData = "정보 없음"
    val nonDate = "00:00"

    const val BASE_URL = "http://124.60.137.10:8080"
    const val photoFilePath = "photo.jpg"
    const val weekFilePath = "week.jpg"
    const val photoFileType = 0
    const val weekFileType = 1

    val emptyMEAL = listOf(
        meals(blank, blank, blank, blank, blank)
    )

    val dummyMEAL = listOf(
        meals(MealType.BREAKFAST.engName, "08:00", "09:30", nonData, blank)
    )
    val dummyMEAL1 = listOf(
        meals(MealType.LUNCH.engName, "11:00", "13:30", nonData, blank)
    )

    fun setInquiryLink(): Intent {
        Log.e("DetailHs1Fragment", "setLocationHyperLink")
        // 카카오톡 오픈 채팅방 하이퍼링크
        val url = "https://open.kakao.com/o/sutXu9ph"
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    // String to Bitmap
    fun stringToBitmap(img: String?): Bitmap? {
        return try {
            val encodeByte = Base64.decode(img, Base64.NO_WRAP)
            val imgByte = Base64.decode(encodeByte, Base64.NO_WRAP)

            if (isNull(encodeByte)) {
                Log.e("utilAll", "stringToBitmap - Error $imgByte")
                return null
            } else Log.e("utilAll", "stringToBitmap - size: ${imgByte.size}")

            val bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)

            if (isNull(bitmap)) {
                Log.e("utilAll", "stringToBitmap - Error $bitmap")
                return null
            } else Log.e("utilAll", "stringToBitmap - size: ${bitmap.width} x ${bitmap.height}")

            bitmap
        } catch (e: Exception) {
            Log.e("utilAll", "stringToBitmap - Error $e")
            null
        }
    }

    // File to Base64
    fun fileToBase64(file: File): String? {
        return try {
            // 1. 파일에서 Bitmap 읽기
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            // 2. 압축 (JPEG + 지정된 품질)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            // 3. ByteArray 추출
            val compressedBytes = outputStream.toByteArray()
            // 4. Base64 인코딩
            Base64.encodeToString(compressedBytes, Base64.NO_WRAP) // Base64로 변환
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
//        val inputStream = FileInputStream(file)
//        val bytes = inputStream.readBytes() // 파일을 바이트 배열로 변환
//        inputStream.close()
//        return Base64.encodeToString(bytes, Base64.DEFAULT) // Base64로 변환
    }

    // 메뉴를 합치는 함수
    fun combineMainAndSub(mainMenu: String?, subMenu: String?): String? {
        if (isNull(mainMenu))
            return null
        else {
            if (isNull(subMenu) || subMenu == blank) {
                return mainMenu?.replaceCommaToLinebreak()
            } else {
                return mainMenu?.replaceCommaToLinebreak() + "\n" + subMenu?.replaceCommaToLinebreak()
            }
        }
    }

    // 해당 일자를 중심으로 수정할 주의 일 리스트 반환
    fun getWeekDates(): List<String> {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")

        val today = LocalDate.now()
        val m: LocalDate
        val t: LocalDate
        val w: LocalDate
        val th: LocalDate
        val f: LocalDate

        // 오늘이 토요일이나 일요일이면 다음 주로 세팅
        if (today.dayOfWeek.name == dOw.SATURDAY.engName || today.dayOfWeek.name == dOw.SUNDAY.engName) {
            m = today.with(DayOfWeek.MONDAY).plusWeeks(1) // 다음 주 월요일
            t = today.with(DayOfWeek.TUESDAY).plusWeeks(1) // 다음 주 월요일
            w = today.with(DayOfWeek.WEDNESDAY).plusWeeks(1) // 다음 주 월요일
            th = today.with(DayOfWeek.THURSDAY).plusWeeks(1) // 다음 주 월요일
            f = today.with(DayOfWeek.FRIDAY).plusWeeks(1)   // 다음 주 금요일
        } else {
            m = today.with(DayOfWeek.MONDAY) // 이번 주 월요일
            t = today.with(DayOfWeek.TUESDAY) // 이번 주 월요일
            w = today.with(DayOfWeek.WEDNESDAY) // 이번 주 월요일
            th = today.with(DayOfWeek.THURSDAY) // 이번 주 월요일
            f = today.with(DayOfWeek.FRIDAY)   // 이번 주 금요일
        }

        // Check
        Log.e(
            "AdminFrgment",
            "getDates: ${m.format(formatter)}, ${t.format(formatter)}, ${w.format(formatter)}, ${
                th.format(
                    formatter
                )
            }, ${f.format(formatter)}"
        )

        // 날짜 리스트로 반환
        return listOf(
            m.format(formatter),
            t.format(formatter),
            w.format(formatter),
            th.format(formatter),
            f.format(formatter)
        )
    }

    // weekStartDate 를 구하는 함수
    fun getWeekStartDate(week: String): String {
        val today = LocalDate.now()
        val formatterDay = DateTimeFormatter.ofPattern("dd")
        val formatterMonth = DateTimeFormatter.ofPattern("MM")
        val formatterYear = DateTimeFormatter.ofPattern("yyyy")

        val nowDay = today.format(formatterDay).toInt()
        val nowMonth = today.format(formatterMonth).toInt()
        val nowYear = today.format(formatterYear).toInt()

        // 현 시점보다 선택한 날짜가 다음 달인지 체크
        if (week.toInt() < nowDay && nowDay > 28) {
            // 다음 달이 다음 년도면
            return if (today.format(formatterMonth).toInt() == 12) {
                LocalDate.of(
                    nowYear + 1,
                    nowMonth + 1,
                    week.toInt()
                ).toString()
            }
            // 이번 년도면
            else
                LocalDate.of(
                    nowYear,
                    nowMonth + 1,
                    week.toInt()
                ).toString()

        }
        // 현재 달과 같다면
        else
            return LocalDate.of(
                nowYear,
                nowMonth,
                week.toInt()
            ).toString()
    }

    // mealType 을 한국어로 변환
    fun mealTypeToKorean(mealType: String?): String {
        if (isNull(mealType))
            return blank
        if (mealType == MealType.BREAKFAST.engName)
            return MealType.BREAKFAST.korName
        else if (mealType == MealType.LUNCH.engName)
            return MealType.LUNCH.korName
        else if (mealType == MealType.DINNER.engName)
            return MealType.DINNER.korName
        else
            return blank
    }

    // dayOfWeek 를 한국어로 변환
    fun dayOfWeekToKorean(dayOfWeek: String): String {
        var result = dayOfWeek
        if (isNull(dayOfWeek))
            return result
        dOw.entries.forEach {
            if (dayOfWeek == it.engName)
                result = it.korName
        }
        return result
    }
}