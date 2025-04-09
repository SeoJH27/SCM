package com.scm.sch_cafeteria_manager.ui.admin.admin1

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
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
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.data.MealType
import com.scm.sch_cafeteria_manager.data.ShareViewModel
import com.scm.sch_cafeteria_manager.data.dailyMeals
import com.scm.sch_cafeteria_manager.data.dataAdmin
import com.scm.sch_cafeteria_manager.data.requestDTO_dayOfWeek
import com.scm.sch_cafeteria_manager.databinding.FragmentAdminHs1Binding
import com.scm.sch_cafeteria_manager.extentions.setTimePickerDialog
import com.scm.sch_cafeteria_manager.util.fetchMealPlans
import com.scm.sch_cafeteria_manager.util.uploadingMealPlans
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.combinMainAndSub
import com.scm.sch_cafeteria_manager.util.utilAll.dayOfWeekToKorean
import com.scm.sch_cafeteria_manager.util.utilAll.fileToBase64
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekDates
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import com.scm.sch_cafeteria_manager.util.utilAll.nonDate
import com.scm.sch_cafeteria_manager.util.utilAll.photoFilePath
import com.scm.sch_cafeteria_manager.util.utilAll.stringToBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects.isNull

class Admin1Hs1WeekFragment : Fragment() {
    private var _binding: FragmentAdminHs1Binding? = null
    private val binding get() = _binding!!
    private val args: Admin1Hs1WeekFragmentArgs by navArgs()
    lateinit var viewModel: ShareViewModel

    var jsonData: dataAdmin? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminHs1Binding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ShareViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 서버로부터 data 받기
        viewLifecycleOwner.lifecycleScope.launch {
            fetchData()
        }
    }

    // 서버로부터 data 받기
    private suspend fun fetchData() {
        binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작
        binding.progressbarBackground.visibility = View.VISIBLE
        binding.progressbarBackground.isClickable = true

        lifecycleScope.launch {
            // Retrofit에서 데이터 가져오기
            try {
                jsonData = fetchMealPlans(
                    requireContext(),
                    CafeteriaData.HYANGSEOL1.cfName,
                    args.manageDate.week,
                    getWeekStartDate(args.manageDate.day)
                )?.data
                Log.e("Admin1Hs1WeekFragment", "fetchAdmin1Hs1Menu: ${jsonData?.dailyMeal}")
            } catch (e: Exception) {
                Log.e(
                    "Admin1Hs1WeekFragment",
                    "fetchAdmin1Hs1Menu Exception: $e, ${getWeekStartDate(args.manageDate.week)}, ${args.manageDate.day}"
                )
                errorToBack()
            }

            // 가져온 데이터 체크
            if (checkData(jsonData)) {
                Log.e("Admin1Hs1WeekFragment", "Data check - Pass")
                setLayout() // UI 세팅
            } else {
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                Log.e(
                    "Admin1Hs1WeekFragment",
                    "Data check - Fail: jsonData=${jsonData?.dailyMeal}"
                )
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
        setPhotoBtnClick()
        setTimeChanger()
        setTextSaveBtnClick()
//        setCheckImage()
        setBack()
    }

    // 한 번 더 해당 요일이 맞는지 체크 후 Layout 세팅
    private fun checkDay() {
        // 데이터가 아예 없는 경우
        if (jsonData?.dailyMeal?.meals == null) {
            with(binding) {
                txtBreakfastOpenTimeStart.text = nonDate
                txtBreakfastOpenTimeEnd.text = nonDate
                txtBreakfastMenu.text = blank

                txtLunchOpenTimeStart.text = nonDate
                txtLunchOpenTimeEnd.text = nonDate
                txtLunchMenu.text = blank

                txtDinnerOpenTimeStart.text = nonDate
                txtDinnerOpenTimeEnd.text = nonDate
                txtDinnerMenu.text = blank
            }
        }
        // 데이터 상 날짜 더블 체크
        else if (jsonData!!.dailyMeal.dayOfWeek == args.manageDate.week) {
            val meals = jsonData!!.dailyMeal.meals!!
            with(binding) {
                toolbarAdminHs1.title =
                    dayOfWeekToKorean(jsonData!!.dailyMeal.dayOfWeek) + " 수정"
                var menu: String?
                if (meals.isNotEmpty()) {
                    // Breakfast
                    txtBreakfastOpenTimeStart.text = meals[0].operatingStartTime
                    txtBreakfastOpenTimeEnd.text = meals[0].operatingEndTime
                    menu = combinMainAndSub(meals[0].mainMenu, meals[0].subMenu)
                    if (isNull(menu)) {
                        txtBreakfastMenu.text = nonDate
                        txtBreakfastMenu.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.grey_300
                            )
                        )
                    } else {
                        txtBreakfastMenu.text = menu
                    }
                }

                if (meals.size > 1) {
                    // Lunch
                    txtLunchOpenTimeStart.text = meals[1].operatingStartTime
                    txtLunchOpenTimeEnd.text = meals[1].operatingEndTime
                    menu = combinMainAndSub(meals[1].mainMenu, meals[1].subMenu)
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
                if (meals.size > 2) {
                    // Dinner
                    txtDinnerOpenTimeStart.text = meals[2].operatingStartTime
                    txtDinnerOpenTimeEnd.text = meals[2].operatingEndTime
                    menu = combinMainAndSub(meals[2].mainMenu, meals[2].subMenu)
                    if (isNull(menu)) {
                        txtDinnerMenu.text = nonDate
                        txtDinnerMenu.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.grey_300
                            )
                        )
                    } else {
                        txtDinnerMenu.text = menu
                    }
                }
            }
        }
    }

    // 촬영하여 등록 버튼 누를 시 -> 촬영
    private fun setPhotoBtnClick() {
        binding.btnUploadWeek.setOnClickListener {
            findNavController().navigate(
                Admin1Hs1WeekFragmentDirections.admin1Hs1ToCamera(
                    false,
                    args.manageDate
                )
            )
        }
    }

    // 시간 선택 세팅
    private fun setTimeChanger() {
        with(binding) {
            txtBreakfastOpenTimeStart.setOnClickListener {
                txtBreakfastOpenTimeStart.setTimePickerDialog(requireContext())
            }
            txtBreakfastOpenTimeEnd.setOnClickListener {
                txtBreakfastOpenTimeEnd.setTimePickerDialog(requireContext())
            }
            txtLunchOpenTimeStart.setOnClickListener {
                txtLunchOpenTimeStart.setTimePickerDialog(requireContext())
            }
            txtLunchOpenTimeEnd.setOnClickListener {
                txtLunchOpenTimeEnd.setTimePickerDialog(requireContext())
            }
            txtDinnerOpenTimeStart.setOnClickListener {
                txtDinnerOpenTimeStart.setTimePickerDialog(requireContext())
            }
            txtDinnerOpenTimeEnd.setOnClickListener {
                txtDinnerOpenTimeEnd.setTimePickerDialog(requireContext())
            }
        }
    }

    // 캐시 이미지 체크
//    private fun setCheckImage() {
//        binding.btnImage.setOnClickListener {
//            val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
//            if (file.exists()) {
//                popUpImage()
//            } else {
//                Toast.makeText(requireContext(), "찍은 사진이 없습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
    // </editor-folder>

    // <editor-folder desc="Save">
    // 서버로 전송 및 뒤로 가기
    private fun setTextSaveBtnClick() {
        binding.btnUploadAllMenu.setOnClickListener {
            val isMenu: requestDTO_dayOfWeek? = getMenu()
            if (isNull(isMenu)) {
                Toast.makeText(requireContext(), "사진이 없어 저장할 수 없습니다.", Toast.LENGTH_LONG).show()
            } else {
                val menu: requestDTO_dayOfWeek = isMenu!!
                with(binding) {
                    progressbar.visibility = View.VISIBLE // UI 블로킹 시작
                    binding.progressbarBackground.visibility = View.VISIBLE
                    binding.progressbarBackground.isClickable = true
                    Log.e("Admin1Hs1WeekFragment", "setTextSaveBtnClick - progressbar")
                    lifecycleScope.launch {
                        try {
                            uploadingMealPlans(
                                requireContext(),
                                CafeteriaData.HYANGSEOL1.cfName,
                                getWeekStartDate(getWeekDates()[0]),
                                menu
                            )
                            cancleImg()
                            backToHome()
                        } catch (e: Exception) {
                            Log.e(
                                "Admin1Hs1WeekFragment",
                                "uploadingMealPlans Exception: $e"
                            )
                            cancleImg()
                            errorToBack()
                        }
                        Log.e("Admin1Hs1WeekFragment", "setTextSaveBtnClick")
                        progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
                        binding.progressbarBackground.visibility = View.GONE
                    }
                }
            }
        }
    }

    // 전송을 위한 데이터 가져오기 TODO: 다시 재정리
    private fun getMenu(): requestDTO_dayOfWeek? {
        if (checkImg()) {
            with(binding) {
                val body =
                    requestDTO_dayOfWeek(
                        getWeekStartDate(args.manageDate.day),
                        dailyMeals(
                            args.manageDate.week,
                            listOf(
                                meals(
                                    MealType.BREAKFAST.myName,
                                    txtBreakfastOpenTimeStart.text.toString(),
                                    txtBreakfastOpenTimeEnd.text.toString(),
                                    txtBreakfastMenu.text.toString(),
                                    blank
                                ),
                                meals(
                                    MealType.LUNCH.myName,
                                    txtLunchOpenTimeStart.text.toString(),
                                    txtLunchOpenTimeEnd.text.toString(),
                                    txtLunchMenu.text.toString(),
                                    blank
                                ),
                                meals(
                                    MealType.DINNER.myName,
                                    txtDinnerOpenTimeStart.text.toString(),
                                    txtDinnerOpenTimeEnd.text.toString(),
                                    txtDinnerMenu.text.toString(),
                                    blank
                                )
                            )
                        ),
                        getImg()
                    )
                Log.e("Admin1Hs1WeekFragment", "getMenu:\n$body")
                return body
            }
        } else {
            Toast.makeText(requireContext(), "사진이 포함되어있지 않습니다.", Toast.LENGTH_LONG).show()
            return null
        }
    }

    // 이미지 가져오기
    private fun getImg(): String {
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        val base64Image = fileToBase64(file)

        return base64Image
    }
    // </editor-folder>

    // <editor-folder desc="Image">
    // 찍은 사진 팝업으로 보여주기
    private fun popUpImage() {
        //TODO: 여백 누르면 사라지게 만들기
        val img = jsonData?.weekMealImg
        Log.e("popUpImage", "$img")

        val builder = Dialog(requireContext())
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.setOnDismissListener { //nothing
        }
        val imageView = ImageView(requireContext())
        imageView.setImageBitmap(stringToBitmap(img))

        builder.addContentView(
            imageView, RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        builder.show()
    }

    // 이미지 체크
    private fun checkImg(): Boolean {
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        return file.exists()
    }

    // 이미지 삭제
    private fun cancleImg() {
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        file.delete()
    }
    // </editor-folder>

    // 데이터 Null-check
    private fun checkData(data: dataAdmin?): Boolean {
        return !(isNull(data) || isNull(data))
    }

    // <editor-folder desc="setBack">
    // 뒤로가기 버튼
    private fun setBack() {
        binding.toolbarAdminHs1.setNavigationOnClickListener {
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

    // 에러 시 뒤로 가기
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