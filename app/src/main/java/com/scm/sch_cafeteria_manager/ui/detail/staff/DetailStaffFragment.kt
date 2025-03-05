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
import com.scm.sch_cafeteria_manager.data.D_API_Response
import com.scm.sch_cafeteria_manager.data.dOw
import com.scm.sch_cafeteria_manager.databinding.FragmentDetailStaffBinding
import com.scm.sch_cafeteria_manager.util.fetchDetailMenu
import com.scm.sch_cafeteria_manager.util.utilAll.setInquiryLink
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Objects.isNull

class DetailStaffFragment : Fragment(R.layout.fragment_detail_staff) {
    private var _binding: FragmentDetailStaffBinding? = null
    private val binding get() = _binding!!

    private var STAFF: D_API_Response? = null // JSON 데이터를 저장할 변수

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

    // 네트워크 통신 -> 코루틴으로 제어
    private fun fetchData() {
        binding.prograssbar.visibility = View.VISIBLE

        Log.e("DetailStaffFragment", "fetchData - prograssbar")
        lifecycleScope.launch {
            // Retrofit에서 데이터 가져오기 (false: staff)
            try {
                STAFF = fetchDetailMenu(false)
                Log.e("DetailStaffFragment", "fetchData - STAFF: $STAFF")
            } catch (e: Error) {
                Log.e("DetailStaffFragment", "fetchData - e: $e")
            }
            Log.e("DetailStaffFragment", "fetchDetailMenu")

            // 데이터 배치
            if (!isNull(STAFF)) {
                Log.e("DetailStaffFragment", "!isNull(STAFF) - STAFF: $STAFF")
                setLayout()
            }
            // 데이터를 불러올 수 없다는 알림과 함께 Back
            else {
                Log.e("DetailStaffFragment", "fetchData - Network Error")
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

    private fun setTab() {
        Log.e("DetailStaffFragment", "setTab")
        with(binding) {
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("월요일"))
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("화요일"))
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("수요일"))
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("목요일"))
            tlDetailStaff.addTab(tlDetailStaff.newTab().setText("금요일"))
        }

        if (checkData()) {
            Log.e("DetailStaffFragment", "setTab - checkData")
            backToHome()
        } else // init tab
            connectAdapter(DayOfWeek.MONDAY.toString())
    }

    private fun setTime() {
        with(binding) {
            if (checkData()) {
                editOperatingHours.setText("") // TODO: 적절히 변경
            } else {
                editOperatingHours.setText("${STAFF!!.data.restaurantOperatingStartTime} ~ ${STAFF!!.data.restaurantOperatingEndTime}")
            }
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
        if (checkData()) {
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
        if (checkData()) {
            Log.e("DetailStaffFragment", "setTab - connectAdapter")
            backToHome()
        } else
            binding.rvDetailStaffMenu.adapter =
                DetailStaffListAdapter(STAFF!!, weekly)
    }

    // 항목이 없으면 true
    private fun checkData(): Boolean {
        Log.e("DetailStaffFragment", "checkData")
        if (isNull(STAFF)) {
            Toast.makeText(requireContext(), "데이터를 로드할 수 없습니다.", Toast.LENGTH_LONG).show()
            return true
        } else return false
    }


    private fun setBackToHome() {
        binding.toolbarDetailStaff.setNavigationOnClickListener {
            backToHome()
        }
    }

    private fun backToHome() {
        findNavController().navigateUp()
    }

    private fun setLocationHyperLink() {
        Log.e("DetailStaffFragment", "setLocationHyperLink")
        // 위치 하이퍼링크
        val url = "https://naver.me/G9rdyWhF"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}