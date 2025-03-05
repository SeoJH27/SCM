package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.icu.util.LocaleData
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
import com.scm.sch_cafeteria_manager.data.TM_API_Response
import com.scm.sch_cafeteria_manager.data.TodayMenu
import com.scm.sch_cafeteria_manager.databinding.FragmentTodayMenuBinding
import com.scm.sch_cafeteria_manager.util.fetchTodayMenu
import com.scm.sch_cafeteria_manager.util.utilAll.doDayOfWeek
import com.scm.sch_cafeteria_manager.util.utilAll.setInquiryLink
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale
import java.util.Objects.isNull

class TodayMenuFragment : Fragment(R.layout.fragment_today_menu) {
    private var _binding: FragmentTodayMenuBinding? = null
    private val binding get() = _binding!!

    private var TODAYMENU: TM_API_Response? = null // JSON 데이터를 저장할 변수

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

    // 네트워크 통신 -> lifecycleScope로 제어
    private fun fetchData() {
        binding.prograssbar.visibility = View.VISIBLE

        Log.e("TodayMenuFragment", "fetchData - prograssbar")
        lifecycleScope.launch {
            // Retrofit에서 데이터 가져오기
            try {
                // 오늘 요일을 기반으로 불러오기
                TODAYMENU = fetchTodayMenu(doDayOfWeek())
                Log.e("DetailHs1Fragment", "fetchTodayMenu - TODAYMENU: $TODAYMENU, ${doDayOfWeek()}")
            } catch (e: Error) {
                Log.e("TodayMenuFragment", "fetchData - e: $e")
            }
            Log.e("TodayMenuFragment", "fetchTodayMenu")

            // 데이터 배치
            if (!isNull(TODAYMENU)) {
                Log.e("TodayMenuFragment", "!isNull(TODAYMENU) - TODAYMENU: $TODAYMENU")
                setLayout()
            }
            // 데이터를 불러올 수 없다는 알림과 함께 Back
            else {
                Log.e("TodayMenuFragment", "fetchData - Network Error")
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다. ${LocalDate.now()}", Toast.LENGTH_SHORT).show()
                backToHome()
            }

            binding.prograssbar.visibility = View.GONE
        }
    }

    private fun setLayout() {
        setTab()
        viewClickListener()
        setBackToHome()
    }

    private fun setTab() {
        Log.e("TodayMenuFragment", "setTab")

        with(binding) {
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText(getStr(R.string.str_hs1)))
//            tlTodayMemu.addTab(tlTodayMemu.newTab().setText(getStr(R.string.str_hs2)))
//            tlTodayMemu.addTab(tlTodayMemu.newTab().setText(getStr(R.string.str_student_union)))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText(getStr(R.string.str_staff)))
        }
        if (checkData()) {
            Log.e("TodayMenuFragment", "setTab - checkData")
            backToHome()
        } else // init tab
            connectAdapter(getStr(R.string.str_hs1))
    }

    private fun viewClickListener() {
        with(binding) {
            binding.tlTodayMemu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    clickTab(tab)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    clickTab(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    connectAdapter(getStr(R.string.str_hs1))
                }
            })
            txtInquiry.setOnClickListener {
                setInquiryLink()
            }
        }
    }

    private fun clickTab(tab: TabLayout.Tab?) {
        if (checkData()) {
            Log.e("TodayMenuFragment", "setTab - clickTab")
            backToHome()
        }

        if (tab == null) {
            Log.e("RecyclerView Tab Error", "Tab이 없음")
            return
        }
        when (tab.position) {
            0 -> connectAdapter(CafeteriaData.HYANGSEOL1.cfName) // 향설 1관 Tab을 눌렸을 경우
//            1 -> connectAdapter(getStr(R.string.str_hs2))
//            2 -> connectAdapter(getStr(R.string.str_student_union))
            1 -> connectAdapter(CafeteriaData.FACULTY.cfName) // 교직원 Tab을 눌렸을 경우
            else -> throw IllegalArgumentException("Invalid button config: $tab")
        }
    }

    private fun connectAdapter(cf: String) {
        Log.e("TodayMenuFragment", "connectAdapter: TODAYMENU = $TODAYMENU")

        if (checkData()) {
            Log.e("TodayMenuFragment", "setTab - connectAdapter")
            backToHome()
        } else {
            binding.rvTodayMenu.adapter =
                TodayMenuListAdapter(TODAYMENU!!, cf)
        }
    }

    // 항목이 없으면 true
    private fun checkData(): Boolean {
        Log.e("TodayMenuFragment", "checkData")
        if (isNull(TODAYMENU)) {
            Toast.makeText(requireContext(), "데이터를 로드할 수 없습니다.", Toast.LENGTH_LONG).show()
            return true
        } else return false
    }

    private fun setBackToHome() {
        binding.tbTodayMenu.setNavigationOnClickListener {
            backToHome()
        }
    }

    private fun backToHome() {
        findNavController().navigateUp()
    }

    private fun getStr(id: Int): String{
        return resources.getString(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}