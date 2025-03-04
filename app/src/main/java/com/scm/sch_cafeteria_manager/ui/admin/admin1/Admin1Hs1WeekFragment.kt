package com.scm.sch_cafeteria_manager.ui.admin.admin1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scm.sch_cafeteria_manager.data.AdminData
import com.scm.sch_cafeteria_manager.databinding.FragmentAdminHs1Binding
import com.scm.sch_cafeteria_manager.util.fetchMealPlans
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Objects.isNull

class Admin1Hs1WeekFragment : Fragment() {
    private var _binding: FragmentAdminHs1Binding? = null
    private val binding get() = _binding!!
    private val args: Admin1Hs1WeekFragmentArgs by navArgs()
    var data: AdminData? = null

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
            fetchDataAndBlockUI(binding.prograssbar)
        }
        setLayout()
        setPhotoBtnClick()
        setTextSaveBtnClick()
        setBack()
    }

    private suspend fun fetchDataAndBlockUI(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE // UI 블로킹 시작

        CoroutineScope(Dispatchers.IO).launch {
            try {
                data = fetchMealPlans(
                    requireContext(),
                    weekStartDate(args.manageDate.week),
                    args.manageDate.day,
                    "HYANGSEOL1"
                )
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
                }
                if (checkData(data)) {
                    errorToBack()
                }
            } catch (e: Error) {
                Log.e("fetchDataAndBlockUI", "error: $e")
                errorToBack()
            }
        }
    }

    private fun errorToBack() {
        Toast.makeText(requireContext(), "로딩할 수 없습니다.", Toast.LENGTH_SHORT).show()
        backToHome()
    }

    // 대기 상태 데이터 불러오기
    private fun setLayout() {
        // 한 번 더 해당 요일이 맞는지 체크
        if (data!!.data.dailyMeals.dayOfWeek == args.manageDate.day){
            val meals = data!!.data.dailyMeals.meals
            with(binding) {
                editBreakfastOpenTimeStart.setText(meals[0].operatingStartTime)
                editBreakfastOpenTimeEnd.setText(meals[0].operatingEndTime)
                editBreakfastMenu.setText(meals[0].mainMenu)

                editLunchOpenTimeStart.setText(meals[1].operatingStartTime)
                editLunchOpenTimeEnd.setText(meals[1].operatingEndTime)
                editLunchMenu.setText(meals[1].mainMenu)

                editDinnerOpenTimeStart.setText(meals[2].operatingStartTime)
                editDinnerOpenTimeEnd.setText(meals[2].operatingEndTime)
                editDinnerMenu.setText(meals[2].mainMenu)
            }
        }
        else{
            errorToBack()
            Log.e("Admin1Hs1WeekFragment", "setLayout - 요일이 맞지 않음")
        }
    }


    // 촬영하여 등록 버튼 누를 시 -> 촬영
    private fun setPhotoBtnClick(){
        findNavController().navigate(Admin1Hs1WeekFragmentDirections.admin1Hs1WeekFragmentToCameraFragment())
    }


    // 텍스트 저장
    private fun setTextSaveBtnClick(){
        binding.btnUploadAllMenu.setOnClickListener {
            //TODO: uploading
        }
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
            if (today.format(formatterMonth).toInt() == 12) {
                return LocalDate.of(
                    nowYear + 1,
                    nowMonth + 1,
                    week.toInt()
                ).toString()
            }
            // 이번 년도면
            else
                return LocalDate.of(
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
        if (isNull(data))
            return false
        else
            return true
    }

    // 뒤로가기 버튼
    fun setBack() {
        // 저장을 누르지 않았을 경우 경고 후 Back
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("뒤로가기 시 저장이 되지 않습니다.\n관리자 홈 화면으로 돌아가시겠습니까?")
            .setNegativeButton("취소") { dialog, which ->
                // 취소 시 아무 액션 없음
            }
            .setPositiveButton("확인") { dialog, which ->
                backToHome()
            }
            .show()
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