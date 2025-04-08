package com.scm.sch_cafeteria_manager.ui.admin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.CafeteriaData
import com.scm.sch_cafeteria_manager.data.NavAdmin
import com.scm.sch_cafeteria_manager.data.ShareViewModel
import com.scm.sch_cafeteria_manager.data.UserRole
import com.scm.sch_cafeteria_manager.data.dOw
import com.scm.sch_cafeteria_manager.data.manageDate
import com.scm.sch_cafeteria_manager.databinding.FragmentAdminBinding
import com.scm.sch_cafeteria_manager.extentions.toEnumOrNull
import com.scm.sch_cafeteria_manager.ui.home.HomeActivity
import com.scm.sch_cafeteria_manager.util.PrefHelper_Login
import com.scm.sch_cafeteria_manager.util.fetchWeekMealPlansMaster
import com.scm.sch_cafeteria_manager.util.logoutToAdmin
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekDates
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import com.scm.sch_cafeteria_manager.util.utilAll.stringToBitmap
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs


class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ShareViewModel

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
    private var authority = UserRole.ADMIN1 // 0: Master, 1: 총관리자, 2: 향설1관, 3: 교직원
    private var isTitleClick: Boolean = false // false: Hyangsheol1, true: faculty
    private val days = getWeekDates()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ShareViewModel::class.java]
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkLogin()
    }

    override fun onResume() {
        super.onResume()
        Log.e("AdminFragment", "onResume")
        when (viewModel.title) {
            CafeteriaData.HYANGSEOL1.cfName -> {
                binding.txtAdminTitle.text = getStr(R.string.str_hs1)
                isTitleClick = false
                Log.e("AdminFragment", "isTitleClick = false")
            }
            CafeteriaData.FACULTY.cfName -> {
                binding.txtAdminTitle.text = getStr(R.string.str_staff)
                isTitleClick = true
                Log.e("AdminFragment", "isTitleClick = true")
            }
            else ->
                Log.e("AdminFragment", "viewModel Error")
        }
        setLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 로그인 체크
        checkLogin()
        // 권한별 접근 제한
        setLayout()
    }

    // <editor-fold desc="setLayout">
    // -----------------------------------------------------------------------------------------------
    private fun setLayout() {
        setAuthority(days)
        setManageDay(days)
        setTodayDate()
        setBackToHome()
        setLogout()
    }

    // <editor-fold desc="setAuthority">
    // -----------------------------------------------------------------------------------------------
    // 권한별 레이아웃 설정
    private fun setAuthority(days: List<String>) {
        when (authority) {
            UserRole.MASTER -> setMaster(days[0])
            UserRole.ADMIN1 -> setAdmin1(days[0])
            UserRole.ADMIN2 -> setAdmin2(days[0])
            UserRole.ADMIN3 -> setAdmin3(days[0])
        }
    }

    // 총관리자 레이아웃
    private fun setAdmin1(day: String) {
        with(binding) {
            icSearch.visibility = View.VISIBLE
            viewAdminTitle.focusable = View.FOCUSABLE
            // 날짜 세팅
            setTodayDate()
            // 세부 날짜 네비게이션 세팅
            if (!isTitleClick) {
                setOpenTimeStartToEnd(
                    getStr(R.string.str_hs1_breakfast_open_time_start),
                    getStr(R.string.str_hs1_dinner_open_time_end)
                )
                setManageMenuNavigateTo(
                    NavAdmin.admin1Hs1.navName,
                    day,
                    CafeteriaData.HYANGSEOL1.cfName
                )
            } else {
                setOpenTimeStartToEnd(
                    getStr(R.string.str_staff_lunch_open_time_start),
                    getStr(R.string.str_staff_lunch_open_time_end)
                )
                setManageMenuNavigateTo(
                    NavAdmin.admin1Staff.navName,
                    day,
                    CafeteriaData.FACULTY.cfName
                )
            }
            // 두 식당 접근
            setChangeTitleAdmin(day)
            // 일주일 치 사진 업로드 세팅
            setWeekPhoto(day)
        }
    }

    // 총관리자: 제목 클릭 시 식당 변경
    private fun setAdmin1TitleClick(day: String) {
        with(binding) {
            // 향설 1관
            if (!isTitleClick) {
                // 고정 시간 변경
                setOpenTimeStartToEnd(
                    getStr(R.string.str_hs1_breakfast_open_time_start),
                    getStr(R.string.str_hs1_dinner_open_time_end)
                )
                // 향설 1관으로 타이틀 변경
                txtAdminTitle.text = getStr(R.string.str_hs1)
                viewModel.changeTitle(CafeteriaData.HYANGSEOL1.cfName)
                // 해당 날짜 표시 및 시간
                setTodayDate()
                // 세부 메뉴의 ClickListener
                setManageMenuNavigateTo(
                    NavAdmin.admin1Hs1.navName,
                    day,
                    CafeteriaData.HYANGSEOL1.cfName
                )
                // 다음 타이틀 클릭 시
                isTitleClick = true
                // 다시 Title 트리거 set
                setChangeTitleAdmin(day)
            }
            // 교직원 식당
            else {
                // 고정 시간 변경
                setOpenTimeStartToEnd(
                    getStr(R.string.str_staff_lunch_open_time_start),
                    getStr(R.string.str_staff_lunch_open_time_end)
                )
                // 교직원 식당으로 타이틀 변경
                txtAdminTitle.text = getStr(R.string.str_staff)
                viewModel.changeTitle(CafeteriaData.FACULTY.cfName)
                // 해당 날짜 표시 및 시간
                setTodayDate()
                // 세부 메뉴의 ClickListener
                setManageMenuNavigateTo(
                    NavAdmin.admin1Staff.navName,
                    day,
                    CafeteriaData.FACULTY.cfName
                )
                // 다음 타이틀 클릭 시
                isTitleClick = false
                // 다시 Title 트리거 set
                setChangeTitleAdmin(day)
            }
        }
    }

    // 향설 1관 레이아웃
    private fun setAdmin2(day: String) {
        with(binding) {
            icSearch.visibility = View.GONE
            viewAdminTitle.focusable = View.NOT_FOCUSABLE

            txtAdminTitle.text = getStr(R.string.str_hs1)
            viewModel.changeTitle(CafeteriaData.HYANGSEOL1.cfName)
            setTodayDate()
            setManageMenuNavigateTo(NavAdmin.admin2.navName, day, CafeteriaData.HYANGSEOL1.cfName)
            // 고정 시간 변경
            setOpenTimeStartToEnd(
                getStr(R.string.str_hs1_breakfast_open_time_start),
                getStr(R.string.str_hs1_dinner_open_time_end)
            )
            // 일주일 치 사진 업로드 세팅
            viewModel.changeTitle(CafeteriaData.HYANGSEOL1.cfName)
            setWeekPhoto(day)
        }
    }

    // 교직원 레이아웃
    private fun setAdmin3(day: String) {
        with(binding) {
            icSearch.visibility = View.GONE
            viewAdminTitle.focusable = View.NOT_FOCUSABLE

            txtAdminTitle.text = getStr(R.string.str_staff)
            viewModel.changeTitle(CafeteriaData.FACULTY.cfName)
            setTodayDate()
            setManageMenuNavigateTo(NavAdmin.admin3.navName, day, CafeteriaData.FACULTY.cfName)
            // 고정 시간 변경
            setOpenTimeStartToEnd(
                getStr(R.string.str_staff_lunch_open_time_start),
                getStr(R.string.str_staff_lunch_open_time_end)
            )
            // 일주일 치 사진 업로드 세팅
            viewModel.changeTitle(CafeteriaData.FACULTY.cfName)
            setWeekPhoto(day)
        }
    }

    //Master 레이아웃
    private fun setMaster(day: String) {
        with(binding) {
            icSearch.visibility = View.VISIBLE
            viewAdminTitle.focusable = View.FOCUSABLE
            // 일주일 치 식단 텍스트 수정 버튼 활성화
            btnTextUploadWeek.focusable = View.FOCUSABLE
            btnTextUploadWeek.visibility = View.VISIBLE
            // 일주일 치 식단 사진 버튼 비활성화
            btnUploadWeek.visibility = View.GONE

            setTodayDate()
            setChangeTitleMaster(day)
            if (!isTitleClick) {
                // 고정 시간 변경
                setOpenTimeStartToEnd(
                    getStr(R.string.str_hs1_breakfast_open_time_start),
                    getStr(R.string.str_hs1_dinner_open_time_end)
                )
                // 일주일 치 사진 보기
                setWeekImgMaster(CafeteriaData.HYANGSEOL1.cfName, day)
                setManageMenuNavigateTo(
                    NavAdmin.masterHs1.navName,
                    day,
                    CafeteriaData.HYANGSEOL1.cfName
                )
            } else {
                // 고정 시간 변경
                setOpenTimeStartToEnd(
                    getStr(R.string.str_staff_lunch_open_time_start),
                    getStr(R.string.str_staff_lunch_open_time_end)
                )
                // 일주일 치 사진 보기
                setWeekImgMaster(CafeteriaData.FACULTY.cfName, day)
                setManageMenuNavigateTo(
                    NavAdmin.masterStaff.navName,
                    day,
                    CafeteriaData.FACULTY.cfName
                )
            }
        }
    }

    // Master 식당 제어
    private fun setMasterTitleClick(day: String) {
        with(binding) {
            // 향설 1관
            if (!isTitleClick) {
                // 고정 시간 변경
                setOpenTimeStartToEnd(
                    getStr(R.string.str_hs1_breakfast_open_time_start),
                    getStr(R.string.str_hs1_dinner_open_time_end)
                )
                // 향설 1관으로 타이틀 변경
                txtAdminTitle.text = getStr(R.string.str_hs1)
                viewModel.changeTitle(CafeteriaData.HYANGSEOL1.cfName)
                // 해당 날짜 표시 및 시간
                setTodayDate()
                // 일주일 메뉴 사진 ClickListener
                setWeekImgMaster(CafeteriaData.HYANGSEOL1.cfName, day)
                // 세부 메뉴의 ClickListener
                setManageMenuNavigateTo(
                    NavAdmin.masterHs1.navName,
                    day,
                    CafeteriaData.HYANGSEOL1.cfName
                )
                // 다음 타이틀 클릭 시
                isTitleClick = true
                // 다시 Title 트리거 set
                setChangeTitleMaster(day)
            }
            // 교직원 식당
            else {
                // 고정 시간 변경
                setOpenTimeStartToEnd(
                    getStr(R.string.str_staff_lunch_open_time_start),
                    getStr(R.string.str_staff_lunch_open_time_end)
                )
                // 교직원 식당으로 타이틀 변경
                txtAdminTitle.text = getStr(R.string.str_staff)
                viewModel.changeTitle(CafeteriaData.FACULTY.cfName)
                // 해당 날짜 표시 및 시간
                setTodayDate()
                // 일주일 메뉴 사진 ClickListener
                setWeekImgMaster(CafeteriaData.FACULTY.cfName, day)
                // 세부 메뉴의 ClickListener
                setManageMenuNavigateTo(
                    NavAdmin.masterStaff.navName,
                    day,
                    CafeteriaData.FACULTY.cfName
                )
                // 다음 타이틀 클릭 시
                isTitleClick = false
                // 다시 Title 트리거 set
                setChangeTitleMaster(day)
            }
        }
    }

    // 일주일 사진 보기 - Master
    private fun setWeekImgMaster(cafeteria: String, day: String) {
        with(binding) {
            btnTextUploadWeek.setOnClickListener {
                progressbar.visibility = View.VISIBLE // UI 블로킹 시작
                binding.progressbarBackground.visibility = View.VISIBLE
                binding.progressbarBackground.isClickable = true
                lifecycleScope.launch {
                    try {
                        val response = fetchWeekMealPlansMaster(
                            requireContext(),
                            getWeekStartDate(day),
                            cafeteria
                        )
                        progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
                        binding.progressbarBackground.visibility = View.GONE

                        if (response != null) {
                            val bitmap = stringToBitmap(response.data.weekMealImg)
                            if (bitmap != null) {
                                // TODO: 이미지 돌리기
//                            val exif = ExifInterface()
//                            val orientation = exif.getAttributeInt(
//                                ExifInterface.TAG_ORIENTATION,
//                                ExifInterface.ORIENTATION_UNDEFINED
//                            )
//                            val b = RotateBitmap(bitmap, orientation)
                                popUpImage(bitmap)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AdminFragment", "setWeekImgMaster Exception: $e")
                    }
                }
            }
        }
    }

    // 세팅된 날짜 표시
    private fun setTodayDate() {
        if (!isTitleClick)
            binding.txtAdminDate.text =
                String.format(
                    resources.getString(R.string.todayDate),
                    LocalDate.now().format(formatter),
                    checkMealTimeHs1()
                )
        else
            binding.txtAdminDate.text =
                String.format(
                    resources.getString(R.string.todayDate),
                    LocalDate.now().format(formatter),
                    checkMealTimeStaff()
                )
    }

    // 식당 변경 - Admin
    private fun setChangeTitleAdmin(day: String) {
        binding.viewAdminTitle.setOnClickListener {
            setAdmin1TitleClick(day)
        }
    }

    // 식당 변경 - Master
    private fun setChangeTitleMaster(day: String) {
        binding.viewAdminTitle.setOnClickListener {
            setMasterTitleClick(day)
        }
    }
    // </editor-fold>

    // 일주일 치
    private fun setWeekPhoto(day: String) {
        binding.btnUploadWeek.setOnClickListener {
            // date 값은 day(시작 일자)만 쓰임
            // true: week, false: dayOfWeek
            when (viewModel.title) {
                CafeteriaData.HYANGSEOL1.cfName -> {
                    navigateTo(
                        NavAdmin.weekCamera.navName,
                        manageDate(dOw.MONDAY.dName, day, CafeteriaData.HYANGSEOL1.cfName),
                        true
                    )
                }

                CafeteriaData.FACULTY.cfName -> {
                    navigateTo(
                        NavAdmin.weekCamera.navName,
                        manageDate(dOw.MONDAY.dName, day, CafeteriaData.FACULTY.cfName),
                        true
                    )
                }

                else -> {
                    Log.e("AdminFragment", "viewModel Error")
                }
            }
        }
    }

    // 일주일 메뉴 속 날짜 설정
    private fun setManageDay(dayOfWeek: List<String>) {
        with(binding) {
            btnMonday.text =
                String.format(
                    resources.getString(R.string.dayOfWeek),
                    dOw.MONDAY.korName,
                    dayOfWeek[0]
                )
            btnTuesday.text =
                String.format(
                    resources.getString(R.string.dayOfWeek),
                    dOw.TUESDAY.korName,
                    dayOfWeek[1]
                )
            btnWednesday.text =
                String.format(
                    resources.getString(R.string.dayOfWeek),
                    dOw.WEDNESDAY.korName,
                    dayOfWeek[2]
                )
            btnThursday.text =
                String.format(
                    resources.getString(R.string.dayOfWeek),
                    dOw.THURSDAY.korName,
                    dayOfWeek[3]
                )
            btnFriday.text =
                String.format(
                    resources.getString(R.string.dayOfWeek),
                    dOw.FRIDAY.korName,
                    dayOfWeek[4]
                )
        }
    }

    // 세부 메뉴를 눌렀을 때
    private fun setManageMenuNavigateTo(
        destination: String,  // 권한 별 프레그먼트
        day: String,   // 일주일 날짜 모음
        cf: String     // 식당 이름
    ) {
        with(binding) {
            btnMonday.setOnClickListener {
                navigateTo(
                    destination, manageDate(dOw.MONDAY.dName, day, cf), null
                )
            }
            btnTuesday.setOnClickListener {
                navigateTo(
                    destination, manageDate(dOw.TUESDAY.dName, day, cf), null
                )
            }
            btnWednesday.setOnClickListener {
                navigateTo(
                    destination, manageDate(dOw.WEDNESDAY.dName, day, cf), null
                )
            }
            btnThursday.setOnClickListener {
                navigateTo(
                    destination, manageDate(dOw.THURSDAY.dName, day, cf), null
                )
            }
            btnFriday.setOnClickListener {
                navigateTo(
                    destination, manageDate(dOw.FRIDAY.dName, day, cf), null
                )
            }
        }
    }
// -----------------------------------------------------------------------------------------------
// </editor-fold>

    // <editor-fold desc="navigation">
    // 일주일 메뉴 네비게이션 설정
    private fun navigateTo(destination: String, date: manageDate, flag: Boolean?) {
        findNavController().navigate(navigateOtherFragment(destination, date, flag))
    }

    // 세부 정보 클릭
    private fun navigateOtherFragment(
        destination: String,
        date: manageDate,
        flag: Boolean?
    ): NavDirections =
        when (destination) {
            NavAdmin.admin1Hs1.navName -> AdminFragmentDirections.toAdmin1Hs1(date)
            NavAdmin.admin1Staff.navName -> AdminFragmentDirections.toAdmin1Staff(date)
            NavAdmin.admin2.navName -> AdminFragmentDirections.toAdmin2(date)
            NavAdmin.admin3.navName -> AdminFragmentDirections.toAdmin3(date)
            NavAdmin.masterHs1.navName -> AdminFragmentDirections.toMasterHs1(date)
            NavAdmin.masterStaff.navName -> AdminFragmentDirections.toMasterStaff(date)
            NavAdmin.weekCamera.navName -> AdminFragmentDirections.toCamera(flag!!, date)
            else -> throw IllegalArgumentException("Invalid button config: $destination")
        }
    // </editor-fold>

    // <editor-fold desc="util">
    // 로그인 체크
    private fun checkLogin() {
        try {
            authority = PrefHelper_Login.getAuthority(requireContext())!!.uppercase()
                .toEnumOrNull<UserRole>()!!
            Log.e("AdminFragment", "authority: $authority")
        } catch (e: Exception) {
            Log.e("AdminFragment", "authority exception: $e")
            Toast.makeText(requireContext(), "로그인 오류", Toast.LENGTH_LONG).show()
            backToHome()
        }
    }

    // 현재 저장된 이미지 팝업으로 띄우기
    @SuppressLint("ClickableViewAccessibility")
    private fun popUpImage(file: Bitmap) {
        val builder = Dialog(requireContext())
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val imageView = ImageView(requireContext())
        imageView.setImageBitmap(file)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        // 드래그 감지
        var downY = 0f
        imageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downY = event.rawY
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val upY = event.rawY
                    val deltaY = upY - downY

                    if (abs(deltaY) > 150) {
                        builder.dismiss()
                    } else {
                        // 클릭으로 간주되는 경우 performClick 호출
                        v.performClick()
                    }
                    true
                }

                else -> false
            }
        }

        imageView.performClick()

        builder.addContentView(
            imageView, RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        builder.show()
    }

    // 리소스 안 텍스트 추출
    private fun getStr(id: Int): String {
        return resources.getString(id)
    }

    // 고정 오픈 시간 세팅
    private fun setOpenTimeStartToEnd(start: String, end: String) {
        binding.txtOpenTimeStart.text = start
        binding.txtOpenTimeEnd.text = end
    }
    // </editor-fold>

    // <editor-fold desc="마감시간">
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
            else -> "\n(식사 시간 아님)"
        }
    }
    // </editor-fold>

    // <editor-fold desc="setBack">
    // Back 네비게이션
    private fun setBackToHome() {
        binding.toolbarAdmin.setNavigationOnClickListener {
            backDialog()
        }
    }

    private fun setLogout() {
        binding.btnLogout.setOnClickListener {
            backDialog()
        }
    }

    // 뒤로가기 전 알림
    private fun backDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("로그아웃하고 홈 화면으로 돌아가시겠습니까?")
            .setNegativeButton("취소") { _, _ ->
                // 취소 시 아무 액션 없음
            }
            .setPositiveButton("확인") { _, _ ->
                lifecycleScope.launch {
                    logoutToAdmin(requireContext())
                }
                backToHome()
            }
            .show()
    }

    // 뒤로가기
    private fun backToHome() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }
// </editor-fold>

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}