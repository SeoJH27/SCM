package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.scm.sch_cafeteria_manager.data.TodayMenu
import com.scm.sch_cafeteria_manager.databinding.FragmentTodayMenuBinding
import com.scm.sch_cafeteria_manager.util.PrefsHelper_TM
import com.scm.sch_cafeteria_manager.util.RetrofitClient_TM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TodayMenuFragment : Fragment() {
    private var _binding: FragmentTodayMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var TM: TodayMenu

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
        setLayout()
    }

    //TODO: 코드 정리
    private fun setLayout() {

        //데이터 받아오기 (TODO: 오류날 시 처리 방도)
        fetchUserData()

        with(binding) {
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("향설 1관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("향설 2관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("학생회관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("교직원 식당"))

            tlTodayMemu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    //TODO: 각 탭에 맞는 데이터를 세팅
                    when (tab!!.position) {
                        0 -> rvTodayMenu.adapter =
                            TodayMenuAdapter(TM, "향설1관") // 향설 1관 Tab을 눌렸을 경우
                        1 -> rvTodayMenu.adapter = TodayMenuAdapter(TM, "향설2관")  // 향설 2관
                        2 -> rvTodayMenu.adapter = TodayMenuAdapter(TM, "학생회관")  // 학생회관
                        3 -> rvTodayMenu.adapter = TodayMenuAdapter(TM, "교직원 식당")  // 교직원 식당
                        else -> throw IllegalArgumentException("Invalid button config: $tab")
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })
        }

        binding.tbTodayMenu.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    // 네트워크 통신 -> 코루틴으로 제어
    fun fetchUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val todayMenu = RetrofitClient_TM.apiService.getTodayMenu()
                println("todayMenu Date: ${todayMenu.weekStartDate}")
                TM = todayMenu
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}