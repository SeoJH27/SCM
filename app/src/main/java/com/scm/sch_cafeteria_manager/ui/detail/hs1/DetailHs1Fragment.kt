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
import com.scm.sch_cafeteria_manager.data.D_API_Response
import com.scm.sch_cafeteria_manager.data.DetailMenu
import com.scm.sch_cafeteria_manager.databinding.FragmentDetailHs1Binding
import com.scm.sch_cafeteria_manager.util.fetchDetailMenu
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Objects.isNull


class DetailHs1Fragment : Fragment(R.layout.fragment_detail_hs1) {
    private var _binding: FragmentDetailHs1Binding? = null
    private val binding get() = _binding!!

    private var HS1: D_API_Response? = null // JSON 데이터를 저장할 변수

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

    // 네트워크 통신 -> 코루틴으로 제어
    private fun fetchData() {
        binding.prograssbar.visibility = View.VISIBLE

        Log.e("DetailHs1Fragment", "fetchData - prograssbar")
        lifecycleScope.launch {
            // Retrofit에서 데이터 가져오기 (true: hs1)
            try {
                HS1 = fetchDetailMenu(true)
                Log.e("DetailHs1Fragment", "fetchData - HS1: $HS1")
            } catch (e: Error) {
                Log.e("DetailHs1Fragment", "fetchData - e: $e")
            }
            Log.e("DetailHs1Fragment", "fetchDetailMenu")

            // 데이터 배치
            if (!isNull(HS1)) {
                Log.e("DetailHs1Fragment", "!isNull(HS1) - HS1: $HS1")
                setLayout()
            }
            // 데이터를 불러올 수 없다는 알림과 함께 Back
            else {
                Log.e("DetailHs1Fragment", "fetchData - Network Error")
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                setBackToHome()
            }

            binding.prograssbar.visibility = View.GONE
        }
    }

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
                    connectAdapter(DayOfWeek.MONDAY.toString())
                }
            })
            csLocation.setOnClickListener {
                setLocationHyperLink()
            }
            txtInquiry.setOnClickListener {
                setInquiryLink()
            }
        }
    }

    private fun setTab() {
        Log.e("DetailHs1Fragment", "setTab")

        with(binding) {
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("월요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("화요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("수요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("목요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("금요일"))
        }

        if (checkData()) {
            Log.e("DetailHs1Fragment", "setTab - checkData")
            backToHome()
        } else // init tab
            connectAdapter(DayOfWeek.MONDAY.toString())
    }

    private fun setTime() {
        with(binding) {
            if (checkData()) {
                editOperatingHours.setText("") // TODO: 적절히 변경
            } else {
                editOperatingHours.setText("${HS1!!.data.restaurantOperatingStartTime} ~ ${HS1!!.data.restaurantOperatingEndTime}")
            }
        }
    }

    // 항목이 없으면 true
    private fun checkData(): Boolean {
        Log.e("DetailHs1Fragment", "checkData")
        if (isNull(HS1)) {
            Toast.makeText(requireContext(), "데이터를 로드할 수 없습니다.", Toast.LENGTH_LONG).show()
            return true
        } else return false
    }

    private fun clickTab(tab: TabLayout.Tab?) {
        if (checkData()) {
            Log.e("DetailHs1Fragment", "setTab - clickTab")
            backToHome()
        }
        if ((tab == null)) {
            Log.e("RecyclerView Tab Error", "Tab이 없음")
            return
        }
        when (tab.position) {
            0 -> connectAdapter(DayOfWeek.MONDAY.toString())
            1 -> connectAdapter(DayOfWeek.TUESDAY.toString())
            2 -> connectAdapter(DayOfWeek.WEDNESDAY.toString())
            3 -> connectAdapter(DayOfWeek.THURSDAY.toString())
            4 -> connectAdapter(DayOfWeek.FRIDAY.toString())
            else -> throw IllegalArgumentException("Invalid button config: $tab")
        }
    }

    private fun connectAdapter(weekly: String) {
        Log.e("DetailHs1Fragment", "connectAdapter: HS1 = $HS1")
        if (checkData()) {
            Log.e("DetailHs1Fragment", "setTab - connectAdapter")
            backToHome()
        } else
            binding.rvDetailHs1Menu.adapter =
                DetailHs1ListAdapter(HS1!!, weekly)
    }

    private fun setBackToHome() {
        binding.toolbarDetailHs1.setNavigationOnClickListener {
            backToHome()
        }
    }

    private fun backToHome() {
        Log.e("DetailHs1Fragment", "backToHome")
        findNavController().navigateUp()
    }

    private fun setLocationHyperLink() {
        Log.e("DetailHs1Fragment", "setLocationHyperLink")
        // 위치 하이퍼링크
        val url = "https://naver.me/5mId7mbJ"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun setInquiryLink() {
        // TODO: 카카오톡 오픈채팅방 하이퍼링크
        Log.e("DetailHs1Fragment", "setInquiryLink")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}