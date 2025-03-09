// TODO: Moshi Test 이후 다시 돌려놓기
//package com.scm.sch_cafeteria_manager.ui.admin.master
//
//import android.app.Dialog
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.Window
//import android.widget.ImageView
//import android.widget.RelativeLayout
//import android.widget.Toast
//import androidx.core.net.toUri
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.NavArgs
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.scm.sch_cafeteria_manager.data.AdminData
//import com.scm.sch_cafeteria_manager.data.CafeteriaData
//import com.scm.sch_cafeteria_manager.data.Daily
//import com.scm.sch_cafeteria_manager.data.Meal
//import com.scm.sch_cafeteria_manager.data.MealType
//import com.scm.sch_cafeteria_manager.data.requestDTO_week_master
//import com.scm.sch_cafeteria_manager.databinding.FragmentMasterHs1Binding
//import com.scm.sch_cafeteria_manager.ui.admin.admin1.Admin1Hs1WeekFragmentDirections
//import com.scm.sch_cafeteria_manager.util.fetchMealPlans
//import com.scm.sch_cafeteria_manager.util.fetchMealPlansMaster
//import com.scm.sch_cafeteria_manager.util.uploadingWeekMealPlansMaster
//import com.scm.sch_cafeteria_manager.util.utilAll.blank
//import com.scm.sch_cafeteria_manager.util.utilAll.photoFilePath
//import kotlinx.coroutines.launch
//import java.io.File
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import java.util.Objects.isNull
//
//class MasterHs1WeekFragment : Fragment() {
//    private var _binding: FragmentMasterHs1Binding? = null
//    private val binding get() = _binding!!
//    private val args: MasterHs1WeekFragmentArgs by navArgs()
//    var data: MasterData? = null
//
//    var weekFlag: Boolean = false
//    var dayOfWeekFlag: Boolean = false
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentMasterHs1Binding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // 서버로부터 data 받기
//        viewLifecycleOwner.lifecycleScope.launch {
//            fetchData()
//        }
//    }
//
//    private fun fetchData() {
//        binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작
//        Log.e("MasterHs1WeekFragment", "fetchData - prograssbar")
//        lifecycleScope.launch {
//            try {
//                data = fetchMealPlansMaster(
//                    requireContext(),
//                    weekStartDate(args.manageDate.day),
//                    args.manageDate.week,
//                    CafeteriaData.HYANGSEOL1.cfName
//                )
//                Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster: $data")
//            } catch (e: Exception) {
//                Log.e(
//                    "MasterHs1WeekFragment",
//                    "fetchMealPlansMaster Exception: $e, ${weekStartDate(args.manageDate.week)}, ${args.manageDate.day}"
//                )
//                errorToBack()
//            }
//            Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster")
//
//            if (checkData(data)) {
//                Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster: checkData - $data")
//                setLayout()
//            } else {
//                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
//                Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster: errorToBack - $data")
//                errorToBack()
//            }
//            binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
//        }
//    }
//
//    // 대기 상태 데이터 불러오기
//    private fun setLayout() {
//        checkDay()
//        setTextSaveBtnClick()
//        setCheckWeekImg()
//        setCheckDayOfWeekImg()
//        setBack()
//    }
//
//    // 한 번 더 해당 요일이 맞는지 체크
//    private fun checkDay() {
//        if (data!!.data.dailyMeal.dayOfWeek == args.manageDate.week) {
//            val meals = data!!.data.dailyMeal.meals
//            with(binding) {
//                txtBreakfastOpenTimeStart.text = meals[0].operatingStartTime
//                txtBreakfastOpenTimeEnd.text = meals[0].operatingEndTime
//                editBreakfastMenu.setText(meals[0].mainMenu)
//
//                txtLunchOpenTimeStart.text = meals[1].operatingStartTime
//                txtLunchOpenTimeEnd.text = meals[1].operatingEndTime
//                editLunchMenu.setText(meals[1].mainMenu)
//
//                txtDinnerOpenTimeStart.text = meals[2].operatingStartTime
//                txtDinnerOpenTimeEnd.text = meals[2].operatingEndTime
//                editDinnerMenu.setText(meals[2].mainMenu)
//            }
//        } else {
//            errorToBack()
//            Log.e("MasterHs1WeekFragment", "setLayout - 요일이 맞지 않음")
//        }
//    }
//
//    // 서버로 전송 및 뒤로가기
//    private fun setTextSaveBtnClick() {
//        binding.btnUploadAllMenu.setOnClickListener {
//            uploadingWeekMealPlansMaster()
//            backToHome()
//        }
//    }
//
//    // 텍스트 업로드
//    // TODO: 버튼 없음, test 필요
//    private fun uploadingWeekMealPlansMaster() {
//        with(binding) {
//            progressbar.visibility = View.VISIBLE // UI 블로킹 시작
//            Log.e("MasterHs1WeekFragment", "uploadingWeekMealPlansMaster - prograssbar")
//            lifecycleScope.launch {
//                try {
//                    val response = uploadingWeekMealPlansMaster(
//                        requireContext(),
//                        CafeteriaData.HYANGSEOL1.cfName,
//                         getMenu()
//                    )
//                    Log.e("MasterHs1WeekFragment", "uploadingWeekMealPlansMaster - ${response?.code}")
//                } catch (e: Exception) {
//                    Log.e(
//                        "MasterHs1WeekFragment",
//                        "uploadingWeekMealPlansMaster Exception: $e"
//                    )
//                    errorToBack()
//                }
//                Log.e("MasterHs1WeekFragment", "uploadingWeekMealPlansMaster")
//                progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
//            }
//        }
//    }
//
//    private fun getMenu(): requestDTO_week_master {
//        with(binding) {
//            val body = requestDTO_week_master(
//                weekStartDate(args.manageDate.day),
//                Daily(
//                    args.manageDate.week, listOf(
//                        Meal(
//                            MealType.BREAKFAST.myNmae, txtBreakfastOpenTimeStart.text.toString(),
//                            txtBreakfastOpenTimeEnd.text.toString(),
//                            editBreakfastMenu.text.toString(),
//                            blank
//                        ),
//                        Meal(
//                            MealType.LUNCH.myNmae, txtLunchOpenTimeStart.text.toString(),
//                            txtLunchOpenTimeEnd.text.toString(),
//                            editLunchMenu.text.toString(),
//                            blank
//                        ),
//                        Meal(
//                            MealType.DINNER.myNmae, txtDinnerOpenTimeStart.text.toString(),
//                            txtDinnerOpenTimeEnd.text.toString(),
//                            editDinnerMenu.text.toString(),
//                            blank
//                        )
//                    )
//                )
//
//            )
//            return body
//        }
//    }
//
//
//    //일주일 이미지 불러오기
//    private fun setCheckWeekImg() {
//        with(binding) {
//            btnWeek.setOnClickListener {
//                if (checkData(data)) {
//                    // 이미지가 사라져야 함
//                    if (weekFlag) {
//                        imgMasterHs1.setImageBitmap(null)
//                        weekFlag = false
//                    }
//                    // 이미지가 보여야 함
//                    else {
//                        imgMasterHs1.setImageBitmap(data?.data?.weekMealImg)
//                        weekFlag = true
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "저장된 이미지가 없습니다.", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//
//    // 해당 요일 이미지 불러오기
//    private fun setCheckDayOfWeekImg() {
//        with(binding) {
//            btnCurrentDay.setOnClickListener {
//                if (checkData(data)) {
//                    // 이미지가 사라져야 함
//                    if (dayOfWeekFlag) {
//                        imgMasterHs1.setImageBitmap(null)
//                        dayOfWeekFlag = false
//                    }
//                    // 이미지가 보여야 함
//                    else {
//                        imgMasterHs1.setImageBitmap(data?.data?.weekMealImg)
//                        dayOfWeekFlag = true
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "저장된 이미지가 없습니다.", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//
//
//    // 해당 버튼에 해당하는 날짜 연산
//    private fun weekStartDate(week: String): String {
//        val today = LocalDate.now()
//        val formatterDay = DateTimeFormatter.ofPattern("dd")
//        val formatterMonth = DateTimeFormatter.ofPattern("MM")
//        val formatterYear = DateTimeFormatter.ofPattern("yyyy")
//
//        val nowDay = today.format(formatterDay).toInt()
//        val nowMonth = today.format(formatterMonth).toInt()
//        val nowYear = today.format(formatterYear).toInt()
//
//        // 현 시점보다 선택한 날짜가 다음 달인지 체크
//        if (week.toInt() > nowDay && nowDay > 20) {
//            // 다음 달이 다음 년도면
//            return if (today.format(formatterMonth).toInt() == 12) {
//                LocalDate.of(
//                    nowYear + 1,
//                    nowMonth + 1,
//                    week.toInt()
//                ).toString()
//            }
//            // 이번 년도면
//            else
//                LocalDate.of(
//                    nowYear,
//                    nowMonth + 1,
//                    week.toInt()
//                ).toString()
//
//        }
//        // 현재 달과 같다면
//        else
//            return LocalDate.of(
//                nowYear,
//                nowMonth,
//                week.toInt()
//            ).toString()
//
//    }
//
//    // 데이터 Null-check
//    private fun checkData(data: MasterData?): Boolean {
//        return !(isNull(data) || isNull(data?.data))
//    }
//
//    // 뒤로가기 버튼
//    private fun setBack() {
//        // 저장을 누르지 않았을 경우 경고 후 Back
//        MaterialAlertDialogBuilder(requireContext())
//            .setMessage("뒤로가기 시 저장이 되지 않습니다.\n관리자 홈 화면으로 돌아가시겠습니까?")
//            .setNegativeButton("취소") { dialog, which ->
//                // 취소 시 아무 액션 없음
//            }
//            .setPositiveButton("확인") { dialog, which ->
//                backToHome()
//            }
//            .show()
//    }
//
//    private fun errorToBack() {
//        Toast.makeText(requireContext(), "로딩할 수 없습니다.", Toast.LENGTH_SHORT).show()
//        backToHome()
//    }
//
//    // 뒤로가기
//    private fun backToHome() {
//        findNavController().navigateUp()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}