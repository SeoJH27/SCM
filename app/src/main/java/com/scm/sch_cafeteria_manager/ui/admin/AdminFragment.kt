package com.scm.sch_cafeteria_manager.ui.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.UserRole
import com.scm.sch_cafeteria_manager.data.dOw
import com.scm.sch_cafeteria_manager.data.manageDate
import com.scm.sch_cafeteria_manager.databinding.FragmentAdminBinding
import com.scm.sch_cafeteria_manager.extentions.toEnumOrNull
import com.scm.sch_cafeteria_manager.ui.home.HomeActivity
import com.scm.sch_cafeteria_manager.util.cacheHelper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    //private var fragmentContext: Context? = null

    val formatter = DateTimeFormatter.ofPattern("dd")
    private var authority = UserRole.ADMIN1 // 0: Master, 1: 총관리자, 2: 향설1관, 3: 교직원
    var isTitleClick = true   // true: 향설 1관, false: 교직원 식당

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            //TODO: 로그인 Test 이후 수정
            authority =
                cacheHelper.readFromCache(requireContext(), "authority")?.toEnumOrNull<UserRole>()!!
            Log.e("AdminFragment", "authority: $authority")
        } catch (e: Exception) {
            Log.e("AdminFragment", "authority exception: $e")
            Toast.makeText(requireContext(), "로그인 오류", Toast.LENGTH_LONG).show()
            backToHome()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val days = getDates()

        // Camera test
        testCamera()

        setManageDay(days)
        setTodayDate()
        // 권한별 접근 제한
        when (authority) {
            UserRole.MASTER -> setMaster(days)
            UserRole.ADMIN1 -> setAdmin1(days)
            UserRole.ADMIN2 -> setAdmin2(days)
            UserRole.ADMIN3 -> setAdmin3(days)
        }
        setBackToHome()
    }


    private fun testCamera(){
        binding.btnEarlyClosed.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.testCamera())
        }
    }


    // 총관리자 레이아웃
    private fun setAdmin1(days: List<String>) {
        with(binding) {
            icSearch.visibility = View.VISIBLE
            viewAdminTitle.focusable = View.FOCUSABLE

            setAdmin1TitleClick(days)

            viewAdminTitle.setOnClickListener {
                setAdmin1TitleClick(days)
            }
        }
    }

    private fun setAdmin1TitleClick(days: List<String>) {
        with(binding) {
            // 향설 1관
            if (isTitleClick) {
                // 향설 1관으로 타이틀 변경
                txtAdminTitle.text = getStr(R.string.str_hs1)
                // 해당 날짜 표시 및 시간
                setTodayDate()
                // 세부 메뉴의 ClickListener
                setManageMenuNavigateTo("admin1Hs1", days)
                // 다음 타이틀 클릭 시
                isTitleClick = false
            }
            // 교직원 식당
            else {
                // 교직원 식당으로 타이틀 변경
                txtAdminTitle.text = getStr(R.string.str_staff)
                // 해당 날짜 표시 및 시간
                setTodayDate()
                // 세부 메뉴의 ClickListener
                setManageMenuNavigateTo("admin1Staff", days)
                // 다음 타이틀 클릭 시
                isTitleClick = true
            }
        }
    }

    private fun setTodayDate() {
        binding.txtAdminDate.setText(
            LocalDate.now().format(formatter) + "일 " + checkMealTimeHs1()
        )
    }

    // 향설 1관 레이아웃
    private fun setAdmin2(days: List<String>) {
        with(binding) {
            icSearch.visibility = View.GONE
            viewAdminTitle.focusable = View.NOT_FOCUSABLE

            txtAdminTitle.text = getStr(R.string.str_hs1)
            setTodayDate()
            setManageMenuNavigateTo("admin2", days)
        }
    }

    // 교직원 레이아웃
    private fun setAdmin3(days: List<String>) {
        with(binding) {
            icSearch.visibility = View.GONE
            viewAdminTitle.focusable = View.NOT_FOCUSABLE

            txtAdminTitle.text = getStr(R.string.str_staff)
            setTodayDate()
            setManageMenuNavigateTo("admin3", days)

        }
    }

    //TODO: 아직 안됨
    //Master 레이아웃
    private fun setMaster(weeks: List<String>) {
        with(binding) {
            icSearch.visibility = View.VISIBLE
            viewAdminTitle.focusable = View.FOCUSABLE
            //TODO:
        }
    }


    // 일주일 메뉴 네비게이션 설정
    private fun navigateTo(destination: String, date: manageDate) {
        findNavController().navigate(navigateOtherFragment(destination, date))
    }

    // 세부 정보 클릭
    private fun navigateOtherFragment(destination: String, date: manageDate): NavDirections =
        when (destination) {
            "admin1Hs1" -> AdminFragmentDirections.toAdmin1Hs1(date)
            "admin1Staff" -> AdminFragmentDirections.toAdmin1Staff(date)
            "admin2" -> AdminFragmentDirections.toAdmin2(date)
            "admin3" -> AdminFragmentDirections.toAdmin3(date)
            "masterHs1" -> AdminFragmentDirections.toMasterHs1(date)
            "masterStaff" -> AdminFragmentDirections.toMasterStaff(date)
            else -> throw IllegalArgumentException("Invalid button config: $destination")
        }

    // 일주일 메뉴 속 날짜 설정
    private fun setManageDay(dayOfWeek: List<String>) {
        with(binding) {
            txtAdminDate

            btnMonday.text = "월요일 (" + dayOfWeek[0] + "일)"
            btnTuesday.text = "화요일 (" + dayOfWeek[1] + "일)"
            btnWednesday.text = "수요일 (" + dayOfWeek[2] + "일)"
            btnThursday.text = "목요일 (" + dayOfWeek[3] + "일)"
            btnFriday.text = "금요일 (" + dayOfWeek[4] + "일)"
        }
    }

    // 오늘 날짜를 기준으로 해당 주의 월-일 날짜 구하기
    fun getDates(): List<String> {
        val today = LocalDate.now()
        val m = today.with(DayOfWeek.MONDAY) // 이번 주 월요일
        val t = today.with(DayOfWeek.TUESDAY) // 이번 주 월요일
        val w = today.with(DayOfWeek.WEDNESDAY) // 이번 주 월요일
        val th = today.with(DayOfWeek.THURSDAY) // 이번 주 월요일
        val f = today.with(DayOfWeek.FRIDAY)   // 이번 주 금요일

        Log.e(
            "AdminFrgment",
            "getDates: ${m.format(formatter)}, ${t.format(formatter)}, ${w.format(formatter)}, ${
                th.format(
                    formatter
                )
            }, ${f.format(formatter)}"
        )
        return listOf(
            m.format(formatter),
            t.format(formatter),
            w.format(formatter),
            th.format(formatter),
            f.format(formatter)
        )
    }

    // 세부 메뉴를 눌렀을 때
    fun setManageMenuNavigateTo(
        destination: String,  // 권한 별 프레그먼트
        days: List<String>   // 일주일 날짜 모음
    ) {
        with(binding) {
            btnMonday.setOnClickListener {
                navigateTo(destination, manageDate(dOw.MONDAY.dName, days[0]))
            }
            btnTuesday.setOnClickListener {
                navigateTo(
                    destination, manageDate(dOw.TUESDAY.dName, days[1])
                )
            }
            btnWednesday.setOnClickListener {
                navigateTo(
                    destination, manageDate(dOw.WEDNESDAY.dName, days[2])
                )
            }
            btnThursday.setOnClickListener {
                navigateTo(
                    destination, manageDate(dOw.THURSDAY.dName, days[3])
                )
            }
            btnFriday.setOnClickListener {
                navigateTo(destination, manageDate(dOw.FRIDAY.dName, days[4]))
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
            else -> return "\n(식사 시간 아님)"
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


    // Back 네비게이션
    private fun setBackToHome() {
        binding.toolbarAdmin.setNavigationOnClickListener {
            backDialog()
        }
    }


    // 뒤로가기 전 알림
    private fun backDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("뒤로가기 시 로그아웃 됩니다.\n홈 화면으로 돌아가시겠습니까?")
            .setNegativeButton("취소") { dialog, which ->
                // 취소 시 아무 액션 없음
            }
            .setPositiveButton("확인") { dialog, which ->
                backToHome()
            }
            .show()
    }

    private fun backToHome() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }

    private fun getStr(id: Int): String {
        return resources.getString(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
