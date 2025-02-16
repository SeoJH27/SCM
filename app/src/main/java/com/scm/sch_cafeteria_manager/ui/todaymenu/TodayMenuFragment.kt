package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.TodayMenu
import com.scm.sch_cafeteria_manager.data.dummy
import com.scm.sch_cafeteria_manager.databinding.FragmentTodayMenuBinding
import com.scm.sch_cafeteria_manager.util.fetchTodayMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Objects.isNull

class TodayMenuFragment : Fragment(R.layout.fragment_today_menu), TodayMenuItemClickListener {
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

    private fun setLayout() {

        //TODO: Test
        TODAYMENU = dummy.tmDummy

        setTab()
        viewClickListener()
        setBackToHome()
    }

    private fun setTab() {
        with(binding) {
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("향설1"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("향설2"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("학생회관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("교직원 식당"))
        }
        // init tab
        connectAdapter("향설1")
    }

    private fun viewClickListener() {
        binding.tlTodayMemu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                clickTab(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                clickTab(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                connectAdapter("향설1")
            }
        })
    }

    private fun clickTab(tab: TabLayout.Tab?) {
        if (tab == null) {
            Log.e("RecyclerView Tab Error", "Tab이 없음")
            return
        }
        when (tab.position) {
            0 -> connectAdapter("향설1") // 향설 1관 Tab을 눌렸을 경우
            1 -> connectAdapter("항설2")
            2 -> connectAdapter("학생회관")
            3 -> connectAdapter("교직원 식당")
            else -> throw IllegalArgumentException("Invalid button config: $tab")
        }
    }

    private fun connectAdapter(cf: String) {
        Log.e("TodayMenuFragment", "connectAdapter")
        binding.rvTodayMenu.adapter =
            TodayMenuListAdapter(TODAYMENU!!, cf, this)
    }

    private fun setBackToHome() {
        binding.tbTodayMenu.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}