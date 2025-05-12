package com.scm.sch_cafeteria_manager.ui.detail.hs1

import android.content.Intent
import android.net.Uri
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
import com.scm.sch_cafeteria_manager.data.UserDetailResponse
import com.scm.sch_cafeteria_manager.data.dOw
import com.scm.sch_cafeteria_manager.databinding.FragmentDetailHs1Binding
import com.scm.sch_cafeteria_manager.util.fetchDetailMenu
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekDates
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import com.scm.sch_cafeteria_manager.util.utilAll.setInquiryLink
import kotlinx.coroutines.launch
import java.util.Objects.isNull


class DetailHs1Fragment : Fragment(R.layout.fragment_detail_hs1) {
    private var _binding: FragmentDetailHs1Binding? = null
    private val binding get() = _binding!!

    private var HS1: UserDetailResponse? = null // JSON 데이터를 저장할 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailHs1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
    }

    // 네트워크 통신 -> lifecycleScope로 제어
    private fun fetchData() {
        binding.progressbar.visibility = View.VISIBLE
        binding.progressbarBackground.visibility = View.VISIBLE
        binding.progressbarBackground.isClickable = true

        lifecycleScope.launch {
            // Retrofit에서 데이터 가져오기
            try {
                val date = getWeekDates()
                HS1 = fetchDetailMenu(CafeteriaData.HYANGSEOL1.cfName, getWeekStartDate(date[0]))
                Log.e("DetailHs1Fragment", "fetchDetailMenu - HS1: ${HS1?.data?.dailyMeals}")
            } catch (e: Error) {
                Log.e("DetailHs1Fragment", "fetchData - e: $e")
                errorToBack()
            }
            // 데이터 배치
            if (checkData(HS1)) {
                Log.e("DetailHs1Fragment", "checkData - HS1: ${HS1?.data?.dailyMeals}")
                setLayout()
            }
            // 데이터를 불러올 수 없다는 알림과 함께 Back
            else {
                Log.e("DetailHs1Fragment", "fetchData - ${HS1?.status}")
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                errorToBack()
            }
            binding.progressbar.visibility = View.GONE
            binding.progressbarBackground.visibility = View.GONE
        }
    }

    // <editor-folder desc="setLayout">
    private fun setLayout() {
        setTab()
        setTime()
        viewClickListener()
        setBackToHome()
    }

    private fun viewClickListener() {
        with(binding) {
            tlDetailHs1.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    clickTab(tab)
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    clickTab(tab)
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    connectAdapter(dOw.MONDAY.engName)
                }
            })
            csLocation.setOnClickListener {
                setLocationHyperLink()
            }
            txtInquiry.setOnClickListener {
                startActivity(setInquiryLink())
            }
        }
    }

    // 상단 탭 설정
    private fun setTab() {
        Log.e("DetailHs1Fragment", "setTab")

        with(binding) {
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("월요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("화요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("수요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("목요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("금요일"))
        }

        if (checkData(HS1)) {
            // init tab
            Log.e("DetailHs1Fragment", "setTab - checkData")
            connectAdapter(dOw.MONDAY.engName)
        } else
            backToHome()
    }

    // 시간 설정
    // TODO: 적절히 변경
    private fun setTime() {
        with(binding) {
            if (checkData(HS1)) {
                editOperatingHours.text = String.format(
                    resources.getString(R.string.timeTotime),
                    HS1!!.data.restaurantOperatingStartTime, HS1!!.data.restaurantOperatingEndTime
                )
            } else {
                editOperatingHours.text = blank

            }
        }
    }
    // </editor-folder>

    private fun clickTab(tab: TabLayout.Tab?) {
        if (!checkData(HS1)) {
            Log.e("DetailHs1Fragment", "setTab - clickTab")
            backToHome()
        }
        if ((tab == null)) {
            Log.e("RecyclerView Tab Error", "Tab 없음")
            return
        }
        when (tab.position) {
            0 -> connectAdapter(dOw.MONDAY.engName)
            1 -> connectAdapter(dOw.TUESDAY.engName)
            2 -> connectAdapter(dOw.WEDNESDAY.engName)
            3 -> connectAdapter(dOw.THURSDAY.engName)
            4 -> connectAdapter(dOw.FRIDAY.engName)
            else -> throw IllegalArgumentException("Invalid button config: $tab")
        }
    }

    private fun connectAdapter(weekly: String) {
        Log.e("DetailHs1Fragment", "connectAdapter: HS1 = $HS1")
        if (checkData(HS1)) {
            binding.rvDetailHs1Menu.adapter =
                DetailHs1ListAdapter(HS1!!, weekly)
        } else {
            Log.e("DetailHs1Fragment", "setTab - connectAdapter")
            backToHome()
        }
    }

    // <editor-folder desc="setBack"
    private fun setBackToHome() {
        binding.toolbarDetailHs1.setNavigationOnClickListener {
            backToHome()
        }
    }

    private fun errorToBack() {
        Toast.makeText(requireContext(), "로딩할 수 없습니다.", Toast.LENGTH_SHORT).show()
        backToHome()
    }

    private fun backToHome() {
        Log.e("DetailHs1Fragment", "backToHome")
        findNavController().navigateUp()
    }
    // </editor-folder>

    private fun setLocationHyperLink() {
        Log.e("DetailHs1Fragment", "setLocationHyperLink")
        // 위치 하이퍼링크
        val url = "https://naver.me/5mId7mbJ"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    // 데이터 Null-check
    private fun checkData(data: UserDetailResponse?): Boolean {
        return !(isNull(data) || isNull(data?.data))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}