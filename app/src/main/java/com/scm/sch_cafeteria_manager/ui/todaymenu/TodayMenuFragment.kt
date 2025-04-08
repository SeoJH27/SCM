package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.CafeteriaData
import com.scm.sch_cafeteria_manager.data.UserTodayMenuResponse
import com.scm.sch_cafeteria_manager.databinding.FragmentTodayMenuBinding
import com.scm.sch_cafeteria_manager.util.fetchTodayMenu
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekDates
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import com.scm.sch_cafeteria_manager.util.utilAll.setInquiryLink
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Objects.isNull

class TodayMenuFragment : Fragment() {
    private var _binding: FragmentTodayMenuBinding? = null
    private val binding get() = _binding!!

    private var TODAYMENU: UserTodayMenuResponse? = null // JSON 데이터를 저장할 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
    }

    // 네트워크 통신 -> lifecycleScope 제어
    private fun fetchData() {
        binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작
        binding.progressbarBackground.visibility = View.VISIBLE
        binding.progressbarBackground.isClickable = true

        Log.e("TodayMenuFragment", "fetchData - progressbar")
        lifecycleScope.launch {
            // Retrofit 데이터 가져오기
            try {
                val today = LocalDate.now().dayOfWeek.name
                val date = getWeekDates()

                TODAYMENU = fetchTodayMenu(today, getWeekStartDate(date[0]))
                Log.e("DetailHs1Fragment", "fetchTodayMenu - TODAYMENU: ${TODAYMENU?.data}")
            } catch (e: Error) {
                Log.e("TodayMenuFragment", "fetchData - e: $e")
                errorToBack()
            }
            // 데이터 배치
            if (checkData(TODAYMENU)) {
                Log.e("TodayMenuFragment", "!isNull(TODAYMENU) - TODAYMENU: $TODAYMENU")
                setLayout()
            }
            // 데이터를 불러올 수 없다는 알림과 함께 Back
            else {
                Log.e("TodayMenuFragment", "fetchData - Network Error")
                Toast.makeText(
                    requireContext(),
                    "학식이 제공 되지 않는 날짜 입니다.\n(${LocalDate.now()})",
                    Toast.LENGTH_SHORT
                ).show()
                backToHome()
            }
            binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
            binding.progressbarBackground.visibility = View.GONE
        }
    }

    // <editor-folder desc="setLayout">
    private fun setLayout() {
        setTab()
        viewClickListener()
        setBackToHome()
    }

    private fun setTab() {
        Log.e("TodayMenuFragment", "setTab")

        with(binding) {
            tlTodayMenu.addTab(tlTodayMenu.newTab().setText(getStr(R.string.str_hs1)))
//            tlTodayMenu.addTab(tlTodayMenu.newTab().setText(getStr(R.string.str_hs2)))
//            tlTodayMenu.addTab(tlTodayMenu.newTab().setText(getStr(R.string.str_student_union)))
            tlTodayMenu.addTab(tlTodayMenu.newTab().setText(getStr(R.string.str_staff)))
        }
        if (checkData(TODAYMENU)) {
            connectAdapter(CafeteriaData.HYANGSEOL1.cfName)
        }
        // init tab
        else {
            Log.e("TodayMenuFragment", "setTab - checkData")
            backToHome()
        }
    }

    private fun viewClickListener() {
        with(binding) {
            binding.tlTodayMenu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    clickTab(tab)
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    clickTab(tab)
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    connectAdapter(CafeteriaData.HYANGSEOL1.cfName)
                }
            })
            txtInquiry.setOnClickListener {
                startActivity(setInquiryLink())
            }
        }
    }

    private fun clickTab(tab: TabLayout.Tab?) {
        if (tab == null) {
            Log.e("RecyclerView Tab Error", "Tab 없음")
            return
        }
        when (tab.position) {
            0 -> connectAdapter(CafeteriaData.HYANGSEOL1.cfName) // 향설 1관 Tab 눌렸을 경우
//            1 -> connectAdapter(getStr(R.string.str_hs2))
//            2 -> connectAdapter(getStr(R.string.str_student_union))
            1 -> connectAdapter(CafeteriaData.FACULTY.cfName) // 교직원 Tab 눌렸을 경우
            else -> throw IllegalArgumentException("Invalid button config: $tab")
        }
    }

    private fun connectAdapter(cf: String) {
        Log.e("TodayMenuFragment", "connectAdapter: TODAYMENU = $TODAYMENU")
        if (checkData(TODAYMENU)) {
            binding.rvTodayMenu.adapter =
                TodayMenuListAdapter(TODAYMENU!!, cf)
        } else {
            Log.e("TodayMenuFragment", "setTab - connectAdapter")
            backToHome()
        }
    }
    // </editor-folder>

    // <editor-folder desc="setBack">
    private fun setBackToHome() {
        binding.tbTodayMenu.setNavigationOnClickListener {
            backToHome()
        }
    }

    // 에러 시 뒤로가기
    private fun errorToBack() {
        Toast.makeText(requireContext(), "로딩할 수 없습니다.", Toast.LENGTH_SHORT).show()
        backToHome()
    }

    private fun backToHome() {
        findNavController().navigateUp()
    }
    // </editor-folder>

    // 데이터 Null-check
    private fun checkData(data: UserTodayMenuResponse?): Boolean {
        return !(isNull(data) || isNull(data?.data))
    }

    private fun getStr(id: Int): String {
        return resources.getString(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}