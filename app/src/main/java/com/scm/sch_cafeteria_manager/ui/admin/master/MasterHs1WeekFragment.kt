package com.scm.sch_cafeteria_manager.ui.admin.master

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.CafeteriaData
import com.scm.sch_cafeteria_manager.data.MasterResponse
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.data.MealType
import com.scm.sch_cafeteria_manager.data.dailyMeals
import com.scm.sch_cafeteria_manager.data.dataMaster
import com.scm.sch_cafeteria_manager.data.requestDTO_week_master
import com.scm.sch_cafeteria_manager.databinding.FragmentMasterHs1Binding
import com.scm.sch_cafeteria_manager.util.fetchMealPlansMaster
import com.scm.sch_cafeteria_manager.util.uploadingWeekMealPlansMaster
import com.scm.sch_cafeteria_manager.util.utilAll.StringToFile
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.combinMainAndSub
import com.scm.sch_cafeteria_manager.util.utilAll.dayOfWeekToKorean
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import com.scm.sch_cafeteria_manager.util.utilAll.nonDate
import com.scm.sch_cafeteria_manager.util.utilAll.saveFileToInternalStorage
import com.scm.sch_cafeteria_manager.util.utilAll.stringToBitmap
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Objects.isNull

class MasterHs1WeekFragment : Fragment() {
    private var _binding: FragmentMasterHs1Binding? = null
    private val binding get() = _binding!!
    private val args: MasterHs1WeekFragmentArgs by navArgs()
    var jsonData: MasterResponse? = null

    var weekFlag: Boolean = false
    var dayOfWeekFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMasterHs1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 서버로부터 data 받기
        viewLifecycleOwner.lifecycleScope.launch {
            fetchData()
        }
    }

    private fun fetchData() {
        binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작
        Log.e("MasterHs1WeekFragment", "fetchData - prograssbar")
        lifecycleScope.launch {
            try {
                jsonData = fetchMealPlansMaster(
                    requireContext(),
                    getWeekStartDate(args.manageDate.day),
                    args.manageDate.week,
                    CafeteriaData.HYANGSEOL1.cfName
                )
                Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster: $jsonData")
            } catch (e: Exception) {
                Log.e(
                    "MasterHs1WeekFragment",
                    "fetchMealPlansMaster Exception: $e, ${getWeekStartDate(args.manageDate.week)}, ${args.manageDate.day}"
                )
                errorToBack()
            }
            Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster")

            if (checkData(jsonData)) {
                Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster: checkData - $jsonData")
                setLayout()
            } else {
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster: errorToBack - $jsonData")
                errorToBack()
            }
            binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
        }
    }

    // <editor-folder desc="setLayout">
    // 대기 상태 데이터 불러오기
    private fun setLayout() {
        checkDay()
        setTextSaveBtnClick()
        setCheckWeekImg()
        setCheckDayOfWeekImg()
        setBack()
    }

    // 한 번 더 해당 요일이 맞는지 체크 후 Layout 세팅
    private fun checkDay() {
        if (jsonData?.data?.dailyMeal == null) {
            with(binding) {
                txtBreakfastOpenTimeStart.text = nonDate
                txtBreakfastOpenTimeEnd.text = nonDate
                edBreakfastMenu.setText(blank)

                txtLunchOpenTimeStart.text = nonDate
                txtLunchOpenTimeEnd.text = nonDate
                edLunchMenu.setText(blank)

                txtDinnerOpenTimeStart.text = nonDate
                txtDinnerOpenTimeEnd.text = nonDate
                edDinnerMenu.setText(blank)
            }
        } else if (jsonData!!.data.dailyMeal.dayOfWeek == args.manageDate.week) {
            val meals = jsonData!!.data.dailyMeal.meals
            with(binding) {
                toolbarAdminHs1.title =
                    dayOfWeekToKorean(jsonData!!.data.dailyMeal.dayOfWeek) + " 수정"
                var menu: String?
                if (meals.size > 0) {
                    txtBreakfastOpenTimeStart.text = meals[0].operatingStartTime
                    txtBreakfastOpenTimeEnd.text = meals[0].operatingEndTime
                    edBreakfastMenu.setText(meals[0].mainMenu)
                    menu = combinMainAndSub(meals[0].mainMenu, meals[0].subMenu)
                    if (isNull(menu)) {
                        edBreakfastMenu.setText(blank)
                    } else {
                        edBreakfastMenu.setText(menu)
                    }
                }
                if (meals.size > 1) {
                    txtLunchOpenTimeStart.text = meals[1].operatingStartTime
                    txtLunchOpenTimeEnd.text = meals[1].operatingEndTime
                    edLunchMenu.setText(meals[1].mainMenu)
                    menu = combinMainAndSub(meals[1].mainMenu, meals[1].subMenu)
                    if (isNull(menu)) {
                        edLunchMenu.setText(blank)
                    } else {
                        edLunchMenu.setText(menu)
                    }
                }
                if (meals.size > 2) {
                    txtDinnerOpenTimeStart.text = meals[2].operatingStartTime
                    txtDinnerOpenTimeEnd.text = meals[2].operatingEndTime
                    edDinnerMenu.setText(meals[2].mainMenu)
                    menu = combinMainAndSub(meals[2].mainMenu, meals[2].subMenu)
                    if (isNull(menu)) {
                        edDinnerMenu.setText(blank)
                    } else {
                        edDinnerMenu.setText(menu)
                    }
                }
            }
        } else {
            errorToBack()
            Log.e("MasterHs1WeekFragment", "setLayout - 요일이 맞지 않음")
        }
    }

    // 서버로 전송 및 뒤로가기
    private fun setTextSaveBtnClick() {
        binding.btnUploadAllMenu.setOnClickListener {
            uploadingWeekMealPlansMaster()
            backToHome()
        }
    }
    // </editor-folder>

    // <editor-folder desc="Save">
    // 텍스트 업로드
    // TODO: 버튼 없음, test 필요
    private fun uploadingWeekMealPlansMaster() {
        with(binding) {
            progressbar.visibility = View.VISIBLE // UI 블로킹 시작
            Log.e("MasterHs1WeekFragment", "uploadingWeekMealPlansMaster - prograssbar")
            lifecycleScope.launch {
                try {
                    val response = uploadingWeekMealPlansMaster(
                        requireContext(),
                        CafeteriaData.HYANGSEOL1.cfName,
                        getMenu()
                    )
                    Log.e(
                        "MasterHs1WeekFragment",
                        "uploadingWeekMealPlansMaster - ${response?.code}"
                    )
                } catch (e: Exception) {
                    Log.e(
                        "MasterHs1WeekFragment",
                        "uploadingWeekMealPlansMaster Exception: $e"
                    )
                    errorToBack()
                }
                Log.e("MasterHs1WeekFragment", "uploadingWeekMealPlansMaster")
                progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
            }
        }
    }

    private fun getMenu(): requestDTO_week_master {
        with(binding) {
            val body = requestDTO_week_master(
                getWeekStartDate(args.manageDate.day),
                dailyMeals(
                    args.manageDate.week, listOf(
                        meals(
                            MealType.BREAKFAST.myNmae, txtBreakfastOpenTimeStart.text.toString(),
                            txtBreakfastOpenTimeEnd.text.toString(),
                            edBreakfastMenu.text.toString(),
                            blank
                        ),
                        meals(
                            MealType.LUNCH.myNmae, txtLunchOpenTimeStart.text.toString(),
                            txtLunchOpenTimeEnd.text.toString(),
                            edLunchMenu.text.toString(),
                            blank
                        ),
                        meals(
                            MealType.DINNER.myNmae, txtDinnerOpenTimeStart.text.toString(),
                            txtDinnerOpenTimeEnd.text.toString(),
                            edDinnerMenu.text.toString(),
                            blank
                        )
                    )
                )

            )
            return body
        }
    }
    // </editor-folder>

    // <editor-folder desc="Image">
    //일주일 이미지 불러 오기
    private fun setCheckWeekImg() {
        with(binding) {
            btnWeek.setOnClickListener {
                if (checkData(jsonData)) {
                    // 이미지가 사라져야 함
                    if (weekFlag) {
                        imgMasterHs1.setImageResource(R.drawable.ic_map)
                        imgMasterHs1.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        imgMasterHs1.requestLayout()
                        weekFlag = false
                    }
                    // 이미지가 보여야 함
                    else {
                        if (jsonData?.data?.weekMealImg != null) {
                            val file = StringToFile(jsonData?.data?.weekMealImg)
                            if (file != null) {
                                println("📂: ${file.absolutePath}")
                            }
//                            imgMasterHs1.setImageBitmap(stringToBitmap(jsonData?.data?.weekMealImg))
                            imgMasterHs1.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                            imgMasterHs1.requestLayout()

                            weekFlag = true
                        } else
                            Toast.makeText(requireContext(), "저장된 이미지가 없습니다.", Toast.LENGTH_LONG)
                                .show()
                    }
                } else {
                    Toast.makeText(requireContext(), "저장된 이미지가 없습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 해당 요일 이미지 불러오기
    private fun setCheckDayOfWeekImg() {
        with(binding) {
            btnCurrentDay.setOnClickListener {
                if (checkData(jsonData)) {
                    // 이미지가 사라져야 함
                    if (dayOfWeekFlag) {
                        imgMasterHs1.setImageDrawable(R.drawable.ic_map.toDrawable())
                        dayOfWeekFlag = false
                    }
                    // 이미지가 보여야 함
                    else {
                        if (jsonData?.data?.weekMealImg != null) {
//                            imgMasterHs1.setImageBitmap(stringToBitmap(jsonData?.data?.weekMealImg))
                            dayOfWeekFlag = true

                        } else
                            Toast.makeText(requireContext(), "저장된 이미지가 없습니다.", Toast.LENGTH_LONG)
                                .show()
                    }
                } else {
                    Toast.makeText(requireContext(), "저장된 이미지가 없습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    // </editor-folder>

    // 데이터 Null-check
    private fun checkData(data: MasterResponse?): Boolean {
        return !(isNull(data) || isNull(data?.data))
    }

    // <editor-folder desc="setBack">
    // 뒤로 가기 버튼
    private fun setBack() {
        binding.toolbarAdminHs1.setNavigationOnClickListener {

            // 저장을 누르지 않았을 경우 경고 후 Back
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("뒤로 가기 시 저장이 되지 않습니다.\n관리자 홈 화면으로 돌아가시겠습니까?")
                .setNegativeButton("취소") { _, _ ->
                    // 취소 시 아무 액션 없음
                }
                .setPositiveButton("확인") { _, _ ->
                    backToHome()
                }
                .show()
        }
    }

    private fun errorToBack() {
        Toast.makeText(requireContext(), "로딩할 수 없습니다.", Toast.LENGTH_SHORT).show()
        backToHome()
    }

    // 뒤로 가기
    private fun backToHome() {
        findNavController().navigateUp()
    }
    // </editor-folder>

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
