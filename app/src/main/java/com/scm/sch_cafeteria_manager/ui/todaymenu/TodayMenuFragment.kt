package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.scm.sch_cafeteria_manager.data.TodayMenu
import com.scm.sch_cafeteria_manager.data.dummy
import com.scm.sch_cafeteria_manager.databinding.FragmentTodayMenuBinding
import com.scm.sch_cafeteria_manager.util.fetchTodayMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Objects.isNull

class TodayMenuFragment : Fragment() {
    private var _binding: FragmentTodayMenuBinding? = null
    private val binding get() = _binding!!
    private var TODAYMENU: TodayMenu? = null // JSON 데이터를 저장할 변수

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
        // TODO: test가 끝나면 변경 - fetchData -> setLayout 순
        setLayout()
    }

    // 네트워크 통신 -> 코루틴으로 제어
    private fun fetchData(view: View) {

        //TODO: Progressbar 생성
        //progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            TODAYMENU = fetchTodayMenu() // Retrofit에서 데이터 가져오기

            if (TODAYMENU != null) {
                //TODO: 데이터 배치 (오류 처리 포함)
                //updateUI(view, todayMenu!!)
            } else {
                // TODO: 데이터를 불러올 수 없다는 알림과 함께 Back
                //textView.text = "데이터를 불러올 수 없습니다."
            }

            //progressBar.visibility = View.GONE
        }
    }

    //TODO: 코드 정리
    private fun setLayout() {

        //TODO: Test
        TODAYMENU = dummy.tmDummy

        with(binding) {
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("향설 1관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("향설 2관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("학생회관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("교직원 식당"))

            tlTodayMemu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (isNull(TODAYMENU)) {
                        throw IllegalArgumentException("TODAYMENU is Null")
                    } else {
                        //TODO: 각 탭에 맞는 데이터를 세팅
                        when (tab!!.position) {
                            0 -> rvTodayMenu.adapter =
                                TodayMenuListAdapter(TODAYMENU!!, "향설1관") // 향설 1관 Tab을 눌렸을 경우
                            1 -> rvTodayMenu.adapter =
                                TodayMenuListAdapter(TODAYMENU!!, "향설2관")  // 향설 2관
                            2 -> rvTodayMenu.adapter =
                                TodayMenuListAdapter(TODAYMENU!!, "학생회관")  // 학생회관
                            3 -> rvTodayMenu.adapter =
                                TodayMenuListAdapter(TODAYMENU!!, "교직원 식당")  // 교직원 식당
                            else -> throw IllegalArgumentException("Invalid button config: $tab")
                        }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}