package com.scm.sch_cafeteria_manager.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.manageDate
import com.scm.sch_cafeteria_manager.databinding.FragmentAdminBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    val formatter = DateTimeFormatter.ofPattern("dd")

    var isTitleClick = true   // true: 향설 1관, false: 교직원 식당

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weeks = getWeekDates()

        setManageDay(weeks)

 
        // TODO:
        //  1. 첫 화면에 주일 계산 필요 **
        //  2. 권한 별 접근 제한 필요


        // 테스트
        //  : 총관리자의 첫 화면. 향설1관으로 시작. (다른 관리자들일 시 돋보기 아이콘을 삭제하고 title 클릭을 비활성화)
        with(binding) {
            viewAdminTitle.setOnClickListener {
                // 향설 1관
                if (isTitleClick) {
                    // 향설 1관으로 타이틀 변경
                    txtAdminTitle.text = R.string.str_hs1.toString()
                    // 해당 날짜 표시 및 시간
                    txtAdminDate.setText(
                        LocalDate.now().format(formatter) + "일 " + checkMealTimeHs1()
                    )
                    // 세부 메뉴의 ClickListener
                    setManageMenuNavigateTo("admin1Hs1", weeks)
                    // 다음 타이틀 클릭 시
                    isTitleClick = false
                }
                // 교직원 식당
                else {
                    txtAdminTitle.text = R.string.str_staff.toString()
                    txtAdminDate.setText(
                        LocalDate.now().format(formatter) + "일 " + checkMealTimeStaff()
                    )
                    setManageMenuNavigateTo("admin1Staff", weeks)
                    isTitleClick = true
                }
            }
        }
    }

    // 일주일 메뉴 네비게이션 설정
    private fun navigateTo(destination: String, date: manageDate) {
        findNavController().navigate(navigateOtherFragment(destination, date))
    }

    private fun navigateOtherFragment(destination: String, date: manageDate): NavDirections =
        when (destination) {
            "admin1Hs1" -> AdminFragmentDirections.toAdmin1Hs1WeekFragment(date)
            "admin1Staff" -> AdminFragmentDirections.toAdmin1StaffWeekFragment(date)
            "admin2" -> AdminFragmentDirections.toAdmin2WeekFragment(date)
            "admin3" -> AdminFragmentDirections.toAdmin3WeekFragment(date)
            "masterHs1" -> AdminFragmentDirections.toMasterHs1WeekFragment(date)
            "masterStaff" -> AdminFragmentDirections.toMasterStaffWeekFragment(date)
            else -> throw IllegalArgumentException("Invalid button config: $destination")
        }

    // 일주일 메뉴 속 날짜 설정
    private fun setManageDay(dayOfWeek: List<String>) {
        with(binding) {
            btnMonday.text = "월요일 (" + dayOfWeek[0] + "일)"
            btnTuesday.text = "화요일 (" + dayOfWeek[1] + "일)"
            btnWednesday.text = "수요일 (" + dayOfWeek[2] + "일)"
            btnThursday.text = "목요일 (" + dayOfWeek[3] + "일)"
            btnFriday.text = "금요일 (" + dayOfWeek[4] + "일)"
        }
    }

    // 오늘 날짜를 기준으로 해당 주의 월-일 날짜 구하기
    fun getWeekDates(): List<String> {
        val today = LocalDate.now()
        val m = today.with(DayOfWeek.MONDAY) // 이번 주 월요일
        val t = today.with(DayOfWeek.TUESDAY) // 이번 주 월요일
        val w = today.with(DayOfWeek.WEDNESDAY) // 이번 주 월요일
        val th = today.with(DayOfWeek.THURSDAY) // 이번 주 월요일
        val f = today.with(DayOfWeek.FRIDAY)   // 이번 주 금요일

        return listOf(m.format(formatter), t.format(formatter), w.format(formatter), th.format(formatter), f.format(formatter))
    }


    // 세부 메뉴를 눌렀을 때
    fun setManageMenuNavigateTo(
        destination: String,  // 권한 별 프레그먼트
        weeks: List<String>   // 일주일 날짜 모음
    ) {
        with(binding) {
            btnMonday.setOnClickListener {
                navigateTo(destination, manageDate(R.string.str_monday.toString(), weeks[0]))
            }
            btnTuesday.setOnClickListener {
                navigateTo(
                    destination,
                    manageDate(R.string.str_tuesday.toString(), weeks[1])
                )
            }
            btnWednesday.setOnClickListener {
                navigateTo(
                    destination,
                    manageDate(
                        R.string.str_wednesday.toString(),
                        weeks[2]
                    )
                )
            }
            btnThursday.setOnClickListener {
                navigateTo(
                    destination,
                    manageDate(R.string.str_thursday.toString(), weeks[3])
                )
            }
            btnFriday.setOnClickListener {
                navigateTo(destination, manageDate(R.string.str_friday.toString(), weeks[4]))
            }
        }
    }


    // 향설 1관 마감 시간
    private fun checkMealTimeHs1(): String {
        val now = LocalTime.now()

        when {
            now.isAfter(LocalTime.of(8, 0)) and now.isBefore(
                LocalTime.of(
                    10,
                    0
                )
            ) -> return "조식"  // 08:00 ~ 9:59
            now.isAfter(LocalTime.of(11, 0)) and now.isBefore(
                LocalTime.of(
                    14,
                    0
                )
            ) -> return "중식"  // 11:00 ~ 13:59
            now.isAfter(LocalTime.of(17, 0)) and now.isBefore(
                LocalTime.of(
                    19,
                    0
                )
            ) -> return "석식"  // 17:00 ~ 18:59
            else -> return ""
        }

    }

    // 교직원 식당 마감 시간
    private fun checkMealTimeStaff(): String {
        val now = LocalTime.now()

        return when {
            now.isAfter(LocalTime.of(11, 30)) and now.isBefore(
                LocalTime.of(
                    14,
                    0
                )
            ) -> "중식"  // 11:30 ~ 13:59
            else -> ""
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
