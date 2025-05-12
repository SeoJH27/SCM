package com.scm.sch_cafeteria_manager.ui.admin.master

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.data.CafeteriaData
import com.scm.sch_cafeteria_manager.data.MasterResponse
import com.scm.sch_cafeteria_manager.data.MealType
import com.scm.sch_cafeteria_manager.data.dailyMeals
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.data.requestDTO_master
import com.scm.sch_cafeteria_manager.databinding.FragmentMasterStaffBinding
import com.scm.sch_cafeteria_manager.extentions.setTimePickerDialog
import com.scm.sch_cafeteria_manager.util.fetchMealPlansMaster
import com.scm.sch_cafeteria_manager.util.uploadingMealPlansMaster
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.combineMainAndSub
import com.scm.sch_cafeteria_manager.util.utilAll.dayOfWeekToKorean
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import kotlinx.coroutines.launch
import java.util.Objects.isNull

class MasterStaffFragment : Fragment() {
    private var _binding: FragmentMasterStaffBinding? = null
    private val binding get() = _binding!!
    private val args: MasterStaffFragmentArgs by navArgs()

    var data: MasterResponse? = null
    var weekFlag: Boolean = false
    var dayOfWeekFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMasterStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작
        binding.progressbarBackground.visibility = View.VISIBLE
        binding.progressbarBackground.isClickable = true
        Log.e("MasterStaffFragment", "fetchData - prograssbar")
        lifecycleScope.launch {
            try {
                data = fetchMealPlansMaster(
                    requireContext(),
                    getWeekStartDate(args.manageDate.day),
                    args.manageDate.week,
                    CafeteriaData.FACULTY.cfName
                )
                Log.e("MasterStaffFragment", "fetchMealPlansMaster: $data")
            } catch (e: Exception) {
                Log.e(
                    "MasterStaffFragment",
                    "fetchMealPlansMaster Exception: $e, ${getWeekStartDate(args.manageDate.week)}, ${args.manageDate.day}"
                )
                errorToBack()
            }
            Log.e("MasterStaffFragment", "fetchMealPlansMaster")

            if (checkData(data)) {
                Log.e("MasterStaffFragment", "fetchMealPlansMaster: checkData - $data")
                setLayout()
            } else {
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                Log.e("MasterHs1WeekFragment", "fetchMealPlansMaster: errorToBack - $data")
                errorToBack()
            }
            binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
            binding.progressbarBackground.visibility = View.GONE
        }
    }

    // <editor-folder desc="setLayout">
    // 대기 상태 데이터 불러오기
    private fun setLayout() {
        checkDay()
        setTimeChanger()
        setTextSaveBtnClick()
        setCheckWeekImg()
        setCheckDayOfWeekImg()
        setBack()
    }

    // 한 번 더 해당 요일이 맞는지 체크 후 Layout 세팅
    private fun checkDay() {
        with(binding) {
            toolbarAdminStaff.title =
                dayOfWeekToKorean(data!!.data.dailyMeal.dayOfWeek) + " 수정"
            btnCurrentDay.text = dayOfWeekToKorean(data!!.data.dailyMeal.dayOfWeek)

            if (data?.data?.dailyMeal == null) {
                edLunchMenu.setText(blank)

            } else if (data!!.data.dailyMeal.dayOfWeek == args.manageDate.week) {
                val meals = data!!.data.dailyMeal.meals!!

                val menu: String?
                if (meals.isNotEmpty()) {
                    txtLunchOpenTimeStart.text = meals[0].operatingStartTime
                    txtLunchOpenTimeEnd.text = meals[0].operatingEndTime
                    edLunchMenu.setText(meals[0].mainMenu)
                    menu = combineMainAndSub(meals[0].mainMenu, meals[0].subMenu)
                    if (isNull(menu)) {
                        edLunchMenu.setText(blank)
                    } else {
                        edLunchMenu.setText(menu)
                    }
                } else {
                    edLunchMenu.setText(blank)
                }
            } else {
                errorToBack()
                Log.e("MasterHs1WeekFragment", "setLayout - 요일이 맞지 않음")
            }
        }
    }

    // 시간 선택 세팅
    private fun setTimeChanger() {
        with(binding) {
            txtLunchOpenTimeStart.setOnClickListener {
                txtLunchOpenTimeStart.setTimePickerDialog(requireContext())
            }
            txtLunchOpenTimeEnd.setOnClickListener {
                txtLunchOpenTimeEnd.setTimePickerDialog(requireContext())
            }
        }
    }
    // 서버로 전송 및 뒤로가기
    private fun setTextSaveBtnClick() {
        binding.btnUploadAllMenu.setOnClickListener {
            uploadingMealPlans()
        }
    }
    // </editor-folder>

    // <editor-folder desc="Save">
    // 텍스트 업로드
    private fun uploadingMealPlans() {
        with(binding) {
            progressbar.visibility = View.VISIBLE // UI 블로킹 시작
            binding.progressbarBackground.visibility = View.VISIBLE
            binding.progressbarBackground.isClickable = true
            Log.e("MasterStaffFragment", "uploadingMealPlans - prograssbar")
            lifecycleScope.launch {
                try {
                    val response = uploadingMealPlansMaster(
                        requireContext(),
                        CafeteriaData.FACULTY.cfName,
                        getMenu()
                    )
                    Log.e(
                        "MasterStaffFragment",
                        "uploadingMealPlansMaster - ${response}"
                    )
                    progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
                    binding.progressbarBackground.visibility = View.GONE

                    if (response == "200")
                        Toast.makeText(requireContext(), "전송 완료", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(requireContext(), "전송 에러: $response", Toast.LENGTH_LONG).show()
                    backToHome()
                } catch (e: Exception) {
                    Log.e(
                        "MasterStaffFragment",
                        "uploadingMealPlansMaster Exception: $e"
                    )
                    if(e.message == "HTTP 400")
                        Toast.makeText(requireContext(), "전송 에러: 빈 데이터가 있습니다.", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(requireContext(), "전송 에러: ${e.message}", Toast.LENGTH_LONG).show()
                    errorToBack()
                }
                Log.e("MasterStaffFragment", "uploadingMealPlansMaster")
            }
        }
    }

    private fun getMenu(): requestDTO_master {
        with(binding) {
            return requestDTO_master(
                getWeekStartDate(args.manageDate.day),
                dailyMeals(
                    args.manageDate.week, listOf(
                        meals(
                            MealType.LUNCH.engName, txtLunchOpenTimeStart.text.toString(),
                            txtLunchOpenTimeEnd.text.toString(),
                            edLunchMenu.text.toString(),
                            blank
                        )
                    )
                )
            )
        }
    }
    // </editor-folder>

    // <editor-folder desc="Image">
    //일주일 이미지 불러 오기
    private fun setCheckWeekImg() {
        with(binding) {
            btnWeek.setOnClickListener {
                try {
                    if (checkData(data)) {
                        // 이미지가 사라져야 함
                        if (weekFlag) {
                            Glide.with(requireContext()).clear(imgMasterStaff)
                            imgMasterStaff.visibility = View.GONE
                            weekFlag = false
                        }
                        // 이미지가 보여야 함
                        else {
                            Glide.with(requireContext()).clear(imgMasterStaff)
                            if (data?.data?.weekMealImg != null) {
                                val encodeByte =
                                    Base64.decode(data?.data?.weekMealImg, Base64.DEFAULT)
                                Log.e(
                                    "MasterHs1Fragment",
                                    "setCheckWeekImg - encodeByte: ${encodeByte.toString(Charsets.UTF_8)}"
                                )

                                Glide.with(requireContext())
                                    .load("data:image/png;base64,${encodeByte.toString(Charsets.UTF_8)}")
                                    .override(450, 650)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .into(imgMasterStaff)
                                imgMasterStaff.visibility = View.VISIBLE

                                weekFlag = true
                            } else
                                Toast.makeText(
                                    requireContext(),
                                    "저장된 이미지가 없습니다.",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "저장된 이미지가 없습니다.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("MasterHs1WeekFragment", "setCheckWeekImg - Error $e")
                }
            }
        }
    }

    // 해당 요일 이미지 불러오기
    private fun setCheckDayOfWeekImg() {
        with(binding) {
            btnCurrentDay.setOnClickListener {
                try {
                    if (checkData(data)) {
                        // 이미지가 사라져야 함
                        if (dayOfWeekFlag) {
                            Glide.with(requireContext()).clear(imgMasterStaff)
                            imgMasterStaff.visibility = View.GONE
                            dayOfWeekFlag = false
                        }
                        // 이미지가 보여야 함
                        else {
                            Glide.with(requireContext()).clear(imgMasterStaff)
                            if (data?.data?.dayMealImg != null) {
                                val encodeByte =
                                    Base64.decode(data?.data?.dayMealImg, Base64.DEFAULT)
                                Log.e(
                                    "MasterHs1Fragment",
                                    "setCheckWeekImg - encodeByte: ${encodeByte.toString(Charsets.UTF_8)}"
                                )
                                Glide.with(requireContext())
                                    .load("data:image/png;base64,${encodeByte.toString(Charsets.UTF_8)}")
                                    .override(450, 650)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .into(imgMasterStaff)
                                imgMasterStaff.visibility = View.VISIBLE

                                dayOfWeekFlag = true
                            } else
                                Toast.makeText(
                                    requireContext(),
                                    "저장된 이미지가 없습니다.",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "저장된 이미지가 없습니다.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception){
                    Log.e("MasterHs1WeekFragment", "setCheckDayOfWeekImg - Error $e")
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
        binding.toolbarAdminStaff.setNavigationOnClickListener {
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
        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
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