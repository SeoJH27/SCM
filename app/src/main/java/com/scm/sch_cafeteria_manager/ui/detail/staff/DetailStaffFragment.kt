package com.scm.sch_cafeteria_manager.ui.detail.staff

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
import com.scm.sch_cafeteria_manager.databinding.FragmentDetailStaffBinding
import com.scm.sch_cafeteria_manager.util.fetchDetailMenu
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekDates
import com.scm.sch_cafeteria_manager.util.utilAll.getWeekStartDate
import com.scm.sch_cafeteria_manager.util.utilAll.setInquiryLink
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Objects.isNull

class DetailStaffFragment : Fragment(R.layout.fragment_detail_staff) {
    private var _binding: FragmentDetailStaffBinding? = null
    private val binding get() = _binding!!

    private var STAFF: UserDetailResponse? = null // JSON 데이터를 저장할 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
    }

    // 네트워크 통신 -> lifecycleScope로 제어
    private fun fetchData() {
        binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작
        binding.progressbarBackground.visibility = View.VISIBLE
        binding.progressbarBackground.isClickable = true
        lifecycleScope.launch {
            // Retrofit에서 데이터 가져오기
            try {
                val date = getWeekDates()
                STAFF = fetchDetailMenu(CafeteriaData.FACULTY.cfName, getWeekStartDate(date[0]))
                Log.e("DetailStaffFragment", "fetchData - STAFF: ${STAFF?.data?.dailyMeals}")
            } catch (e: Error) {
                Log.e("DetailStaffFragment", "fetchData - e: $e")
                errorToBack()
            }
            // 데이터 배치
            if (checkData(STAFF)) {
                Log.e("DetailStaffFragment", "!isNull(STAFF) - STAFF: $STAFF")
                setLayout()
            }
            // 데이터를 불러올 수 없다는 알림과 함께 Back
            else {
                Log.e("DetailStaffFragment", "fetchData - message: ${STAFF?.message}")
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                backToHome()
            }
            binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
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

    // TODO: 적절히 변경
    private fun setTime() {
        with(binding) {
            if (checkData(STAFF)) {
                editOperatingHours.text = String.format(
                    resources.getString(R.string.timeTotime),
                    STAFF!!.data.restaurantOperatingStartTime,
                    STAFF!!.data.restaurantOperatingEndTime
                )
            } else {
                editOperatingHours.text = blank
            }
        }
    }

    private fun setTab() {
        Log.e("DetailStaffFragment", "setTab")
        with(binding) {
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("월요일"))
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("화요일"))
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("수요일"))
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("목요일"))
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("금요일"))
        }

        if (checkData(STAFF)) {// init tab
            connectAdapter(dOw.MONDAY.dName)
        } else {
            Log.e("DetailStaffFragment", "setTab - checkData")
            backToHome()
        }
    }

    private fun viewClickListener() {
        with(binding) {
            tlDetailStaff.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    clickTab(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    clickTab(tab)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    connectAdapter(dOw.MONDAY.dName)
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

    private fun clickTab(tab: TabLayout.Tab?) {
        if (!checkData(STAFF)) {
            Log.e("DetailStaffFragment", "setTab - clickTab")
            backToHome()
        }
        if ((tab == null)) {
            Log.e("RecyclerView Tab Error", "Tab이 없음")
            return
        }
        when (tab.position) {
            0 -> connectAdapter(dOw.MONDAY.dName)
            1 -> connectAdapter(dOw.TUESDAY.dName)
            2 -> connectAdapter(dOw.WEDNESDAY.dName)
            3 -> connectAdapter(dOw.THURSDAY.dName)
            4 -> connectAdapter(dOw.FRIDAY.dName)
            else -> throw IllegalArgumentException("Invalid button config: $tab")
        }
    }

    private fun connectAdapter(weekly: String) {
        Log.e("DetailStaffFragment", "connectAdapter: STAFF = $STAFF")
        if (checkData(STAFF)) {
            binding.rvDetailStaffMenu.adapter =
                DetailStaffListAdapter(STAFF!!, weekly)
        } else {
            Log.e("DetailStaffFragment", "setTab - connectAdapter")
            backToHome()
        }
    }
    // </editor-folder>

    // <editor-folder desc="setBack">
    private fun setBackToHome() {
        binding.toolbarDetailStaff.setNavigationOnClickListener {
            backToHome()
        }
    }

    private fun errorToBack() {
        Toast.makeText(requireContext(), "로딩할 수 없습니다.", Toast.LENGTH_SHORT).show()
        backToHome()
    }

    private fun backToHome() {
        findNavController().navigateUp()
    }
    // </editor-folder>

    private fun setLocationHyperLink() {
        Log.e("DetailStaffFragment", "setLocationHyperLink")
        // 위치 하이퍼링크
        val url = "https://naver.me/G9rdyWhF"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    // 데이터 Null-check: true-데이터 있음, false-데이터 없음
    private fun checkData(data: UserDetailResponse?): Boolean {
        return !(isNull(data) || isNull(data?.data))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}