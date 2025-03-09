package com.scm.sch_cafeteria_manager.ui.admin.admin1

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.data.AdminData
import com.scm.sch_cafeteria_manager.data.CafeteriaData
import com.scm.sch_cafeteria_manager.data.Daily
import com.scm.sch_cafeteria_manager.data.Meal
import com.scm.sch_cafeteria_manager.data.MealType
import com.scm.sch_cafeteria_manager.data.requestDTO_dayOfWeek
import com.scm.sch_cafeteria_manager.databinding.FragmentAdminHs1Binding
import com.scm.sch_cafeteria_manager.extentions.setTimePickerDialog
import com.scm.sch_cafeteria_manager.util.fetchMealPlans
import com.scm.sch_cafeteria_manager.util.uploadingMealPlans
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.nonDate
import com.scm.sch_cafeteria_manager.util.utilAll.photoFilePath
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Objects.isNull

class Admin1Hs1WeekFragment : Fragment() {
    private var _binding: FragmentAdminHs1Binding? = null
    private val binding get() = _binding!!
    private val args: Admin1Hs1WeekFragmentArgs by navArgs()
    var data: AdminData? = null

    var jsonReader: AdminData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminHs1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 서버로부터 data 받기
        viewLifecycleOwner.lifecycleScope.launch {
            fetchData()
        }
    }

    // 한 번 더 해당 요일이 맞는지 체크
//    private fun checkDay() {
//        if (data?.data.dailyMeals == null) {
//            with(binding) {
//                txtBreakfastOpenTimeStart.text = nonDate
//                txtBreakfastOpenTimeEnd.text = nonDate
//                editBreakfastMenu.text = blank
//
//                txtLunchOpenTimeStart.text = nonDate
//                txtLunchOpenTimeEnd.text = nonDate
//                editLunchMenu.text = blank
//
//                txtDinnerOpenTimeStart.text = nonDate
//                txtDinnerOpenTimeEnd.text = nonDate
//                editDinnerMenu.text = blank
//            }
//        } else if (data!!.data.dailyMeals!!.dayOfWeek == args.manageDate.week) {
//            val meals = data!!.data.dailyMeals!!.meals
//            with(binding) {
//                toolbarAdminHs1.title = data!!.data.dailyMeals!!.dayOfWeek + " 수정"
//
//                txtBreakfastOpenTimeStart.text = meals[0].operatingStartTime ?: nonDate
//                txtBreakfastOpenTimeEnd.text = meals[0].operatingEndTime ?: nonDate
//                editBreakfastMenu.text = meals[0].mainMenu
//
//                txtLunchOpenTimeStart.text = meals[1].operatingStartTime ?: nonDate
//                txtLunchOpenTimeEnd.text = meals[1].operatingEndTime ?: nonDate
//                editLunchMenu.text = meals[1].mainMenu
//
//                txtDinnerOpenTimeStart.text = meals[2].operatingStartTime ?: nonDate
//                txtDinnerOpenTimeEnd.text = meals[2].operatingEndTime ?: nonDate
//                editDinnerMenu.text = meals[2].mainMenu
//            }
//        }
//    }

    private suspend fun fetchData() {
        binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작

        Log.e("Admin1Hs1WeekFragment", "fetchData - prograssbar")
        lifecycleScope.launch {
            // Retrofit에서 데이터 가져오기
            try {
                // TODO : Moshi 테스트중
                jsonReader = fetchMealPlans(
                    requireContext(),
                    CafeteriaData.HYANGSEOL1.cfName,
                    args.manageDate.week,
                    weekStartDate(args.manageDate.day)
                )
                Log.e("Admin1Hs1WeekFragment", "fetchAdmin1Hs1Menu: ${data?.data}")
            } catch (e: Exception) {
                Log.e(
                    "Admin1Hs1WeekFragment",
                    "fetchAdmin1Hs1Menu Exception: $e, ${weekStartDate(args.manageDate.week)}, ${args.manageDate.day}"
                )
                errorToBack()
            }
            Log.e("Admin1Hs1WeekFragment", "fetchAdmin1Hs1Menu")

            if (checkData(data)) {
                Log.e(
                    "Admin1Hs1WeekFragment",
                    "fetchAdmin1Hs1Menu: checkData - ${data?.data}"
                )
                setLayout()
            } else {
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                Log.e("Admin1Hs1WeekFragment", "fetchAdmin1Hs1Menu: errorToBack - $data")
                errorToBack()
            }

            binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
        }
    }

    // 대기 상태 데이터 불러오기
    private fun setLayout() {
        moshiData()
//        checkDay()
//        setPhotoBtnClick()
//        setTimeChanger()
//        setTextSaveBtnClick()
//        setCheckImage()
//        setBack()
    }

    private fun moshiData(){
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(Admin1Hs1WeekFragment::class.java)

        if (isNull(jsonReader)){
            errorToBack()
        }else{

            Log.e("Admin1Hs1WeekFragment", "Moshi Test : ${jsonReader?.data}")

        }
    }

    // 촬영하여 등록 버튼 누를 시 -> 촬영
    private fun setPhotoBtnClick() {
        binding.btnUploadWeek.setOnClickListener {
            findNavController().navigate(Admin1Hs1WeekFragmentDirections.admin1Hs1ToCamera())
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

    // 서버로 전송 및 뒤로가기
    private fun setTextSaveBtnClick() {
        binding.btnUploadAllMenu.setOnClickListener {
            val isMenu: requestDTO_dayOfWeek? = getMenu()
            if (isNull(isMenu))
                Toast.makeText(requireContext(), "사진이 없어 저장할 수 없습니다.", Toast.LENGTH_LONG).show()
            else {
                val menu: requestDTO_dayOfWeek = isMenu!!
                with(binding) {
                    progressbar.visibility = View.VISIBLE // UI 블로킹 시작
                    Log.e("Admin1Hs1WeekFragment", "setTextSaveBtnClick - prograssbar")
                    lifecycleScope.launch {
                        try {
                            val response = uploadingMealPlans(
                                requireContext(),
                                CafeteriaData.HYANGSEOL1.cfName,
                                menu
                            )
                            Log.e(
                                "Admin1Hs1WeekFragment",
                                "uploadingMealPlans - ${response?.code}"
                            )
                        } catch (e: Exception) {
                            Log.e(
                                "Admin1Hs1WeekFragment",
                                "uploadingMealPlans Exception: $e"
                            )
                            errorToBack()
                        }
                        Log.e("Admin1Hs1WeekFragment", "setTextSaveBtnClick")
                        progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
                    }
                }
            }
        }
    }

    private fun getMenu(): requestDTO_dayOfWeek? {
        if (checkImg()) {
            with(binding) {
                val body = requestDTO_dayOfWeek(
                    weekStartDate(args.manageDate.day),
                    Daily(
                        args.manageDate.week, listOf(
                            Meal(
                                MealType.BREAKFAST.myNmae,
                                txtBreakfastOpenTimeStart.text.toString(),
                                txtBreakfastOpenTimeEnd.text.toString(),
                                editBreakfastMenu.text.toString(),
                                blank
                            ),
                            Meal(
                                MealType.LUNCH.myNmae, txtLunchOpenTimeStart.text.toString(),
                                txtLunchOpenTimeEnd.text.toString(), editLunchMenu.text.toString(),
                                blank
                            ),
                            Meal(
                                MealType.DINNER.myNmae,
                                txtDinnerOpenTimeStart.text.toString(),
                                txtDinnerOpenTimeEnd.text.toString(),
                                editDinnerMenu.text.toString(),
                                blank
                            )
                        )
                    ),
                    getImg()
                )
                return body
            }
        } else {
            Toast.makeText(requireContext(), "사진이 포함되어있지 않습니다.", Toast.LENGTH_LONG).show()
            return null
        }
    }

    // 캐시 이미지 체크
    private fun setCheckImage() {
        binding.btnImage.setOnClickListener {
            val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
            if (file.exists()) {
                popUpImage(file)
            } else {
                Toast.makeText(requireContext(), "찍은 사진이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 찍은 사진 팝업으로 보여주기
    private fun popUpImage(file: File) {
        val builder = Dialog(requireContext())
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        builder.setOnDismissListener {
            //nothing
        }
        val imageView = ImageView(requireContext())
        imageView.setImageURI(file.toUri())
        builder.addContentView(
            imageView, RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        builder.show()
    }

    // 이미지 가져오기
    private fun getImg(): Bitmap {
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        return BitmapFactory.decodeFile(file.absolutePath);
    }

    // 해당 버튼에 해당하는 날짜 연산
    private fun weekStartDate(week: String): String {
        val today = LocalDate.now()
        val formatterDay = DateTimeFormatter.ofPattern("dd")
        val formatterMonth = DateTimeFormatter.ofPattern("MM")
        val formatterYear = DateTimeFormatter.ofPattern("yyyy")

        val nowDay = today.format(formatterDay).toInt()
        val nowMonth = today.format(formatterMonth).toInt()
        val nowYear = today.format(formatterYear).toInt()

        // 현 시점보다 선택한 날짜가 다음 달인지 체크
        if (week.toInt() > nowDay && nowDay > 20) {
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

    // 데이터 Null-check
    private fun checkData(data: AdminData?): Boolean {
        return !(isNull(data) || isNull(data?.data))
    }

    private fun checkImg(): Boolean {
        val file = File(requireContext().externalCacheDirs?.firstOrNull(), photoFilePath)
        return file.exists()
    }

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
    }

    private fun errorToBack() {
        Toast.makeText(requireContext(), "로딩할 수 없습니다.", Toast.LENGTH_SHORT).show()
        backToHome()
    }

    // 뒤로가기
    private fun backToHome() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}