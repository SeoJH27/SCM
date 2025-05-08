package com.scm.sch_cafeteria_manager.ui.admin.admin3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.CafeteriaData
import com.scm.sch_cafeteria_manager.data.MealType
import com.scm.sch_cafeteria_manager.data.ShareViewModel
import com.scm.sch_cafeteria_manager.data.dailyMeals
import com.scm.sch_cafeteria_manager.data.dataAdmin
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.data.requestDTO_dayOfWeek
import com.scm.sch_cafeteria_manager.databinding.FragmentAdminStaffBinding
import com.scm.sch_cafeteria_manager.extentions.setTimePickerDialog
import com.scm.sch_cafeteria_manager.util.fetchMealPlans
import com.scm.sch_cafeteria_manager.util.uploadingMealPlans
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.combineMainAndSub
import com.scm.sch_cafeteria_manager.util.utilAll.dayOfWeekToKorean
import com.scm.sch_cafeteria_manager.util.utilAll.fileToBase64
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekDates
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import com.scm.sch_cafeteria_manager.util.utilAll.nonDate
import com.scm.sch_cafeteria_manager.util.utilAll.photoFilePath
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects.isNull

// 교직원 식당 관리자
class Admin3WeekFragment : Fragment() {
    private var _binding: FragmentAdminStaffBinding? = null
    private val binding get() = _binding!!
    private val args: Admin3WeekFragmentArgs by navArgs()
    private lateinit var viewModel: ShareViewModel

    private var jsonData: dataAdmin? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminStaffBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ShareViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            fetchData()
        }
    }

    private suspend fun fetchData() {
        binding.progressbar.visibility = View.VISIBLE
        binding.progressbarBackground.visibility = View.VISIBLE
        binding.progressbarBackground.isClickable = true

        lifecycleScope.launch {
            try {
                jsonData = fetchMealPlans(
                    requireContext(),
                    CafeteriaData.FACULTY.cfName,
                    args.manageDate.week,
                    getWeekStartDate(args.manageDate.day)
                )?.data
                Log.e("Admin3WeekFragment", "fetchAdmin3FacultyMenu: ${jsonData?.dailyMeal}")
            } catch (e: Exception) {
                Log.e(
                    "Admin3WeekFragment",
                    "fetchAdmin3FacultyMenu Exception: $e, ${getWeekStartDate(args.manageDate.week)}, ${args.manageDate.day}"
                )
                errorToBack()
            }

            if (checkData(jsonData)) {
                Log.e("Admin3WeekFragment", "Data check - Pass")
                setLayout()
            } else {
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_LONG).show()
                Log.e(
                    "Admin3WeekFragment",
                    "Data check - Fail: jsonData=${jsonData?.dailyMeal}"
                )
                errorToBack()
            }
            binding.progressbar.visibility = View.GONE
            binding.progressbarBackground.visibility = View.GONE
        }
    }

    private fun setLayout() {
        checkDay()
        setPhotoBtnClick()
        setTimeChanger()
        setTextSaveBtnClick()
        setBack()
    }

    private fun checkDay() {
        with(binding) {
            toolbarAdminStaff.title =
                dayOfWeekToKorean(jsonData!!.dailyMeal.dayOfWeek) + " 수정"

            if (jsonData?.dailyMeal == null) {
                txtLunchMenu.text = blank

            } else if (jsonData!!.dailyMeal.dayOfWeek == args.manageDate.week) {
                val meals = jsonData!!.dailyMeal.meals!!
                val menu: String?
                if (meals.isNotEmpty()) {
                    txtLunchOpenTimeStart.text = meals[0].operatingStartTime
                    txtLunchOpenTimeEnd.text = meals[0].operatingEndTime
                    menu = combineMainAndSub(meals[0].mainMenu, meals[0].subMenu)
                    if (isNull(menu)) {
                        txtLunchMenu.text = nonDate
                        txtLunchMenu.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.grey_300
                            )
                        )
                    } else {
                        txtLunchMenu.text = menu
                    }
                }
            }
        }
    }

    private fun setPhotoBtnClick() {
        binding.btnCaptureImage.setOnClickListener {
            findNavController().navigate(
                Admin3WeekFragmentDirections.admin3ToCamera(
                    false,
                    args.manageDate
                )
            )
        }
    }

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

    private fun setTextSaveBtnClick() {
        binding.btnUploadAllMenu.setOnClickListener {
            val isMenu: requestDTO_dayOfWeek? = getMenu()
            if (isNull(isMenu)) {
                Toast.makeText(requireContext(), "사진이 없어 저장할 수 없습니다.", Toast.LENGTH_LONG)
                    .show()
            } else {
                val menu: requestDTO_dayOfWeek = isMenu!!
                with(binding) {
                    progressbar.visibility = View.VISIBLE
                    Log.e("Admin3WeekFragment", "setTextSaveBtnClick - progressbar")
                    lifecycleScope.launch {
                        try {
                            uploadingMealPlans(
                                requireContext(),
                                CafeteriaData.FACULTY.cfName,
                                getWeekStartDate(getWeekDates()[0]),
                                menu
                            )
                            cancelImg()
                            backToHome()
                        } catch (e: Exception) {
                            Log.e(
                                "Admin3WeekFragment",
                                "uploadingMealPlans Exceptioin: $e"
                            )
                            cancelImg()
                            errorToBack()
                        }
                        Log.e("Admin3WeekFragment", "setTextSaveBtnClick")
                        progressbar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun getMenu(): requestDTO_dayOfWeek? {
        if (checkImg()) {
            val body =
                requestDTO_dayOfWeek(
                    getWeekStartDate(args.manageDate.day),
                    dailyMeals(
                        args.manageDate.week,
                        listOf(
                            meals(
                                MealType.LUNCH.myName,
                                binding.txtLunchOpenTimeStart.text.toString(),
                                binding.txtLunchOpenTimeEnd.text.toString(),
                                binding.txtLunchMenu.text.toString(),
                                blank
                            )
                        )
                    ),
                    getImg()
                )
            Log.e("Admin3WeekFragment", "getMenu:\n$body")
            return body
        } else {
            Toast.makeText(requireContext(), "사진이 포함 되어 있지 않습니다.", Toast.LENGTH_LONG).show()
            return null
        }
    }

    private fun getImg(): String {
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        val base64Img = fileToBase64(file)
        return base64Img
    }

    private fun checkImg(): Boolean {
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        return file.exists()
    }

    private fun cancelImg() {
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        file.delete()
    }

    private fun checkData(data: dataAdmin?): Boolean {
        return !(isNull(data) || isNull(data))
    }

    private fun setBack() {
        binding.toolbarAdminStaff.setNavigationOnClickListener {
            // 저장을 누르지 않았을 경우 경고 후 Back
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("뒤로가기 시 저장이 되지 않습니다.\n관리자 홈 화면으로 돌아가시겠습니까?")
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
        Toast.makeText(requireContext(), "로딩할 수 없습니다.", Toast.LENGTH_LONG).show()
        backToHome()
    }

    private fun backToHome(){
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}