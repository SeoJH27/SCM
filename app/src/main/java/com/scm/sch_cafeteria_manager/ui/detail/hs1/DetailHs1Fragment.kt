package com.scm.sch_cafeteria_manager.ui.detail.hs1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.DetailMenu
import com.scm.sch_cafeteria_manager.data.dummy
import com.scm.sch_cafeteria_manager.databinding.FragmentDetailHs1Binding
import com.scm.sch_cafeteria_manager.util.fetchDetailMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailHs1Fragment : Fragment(R.layout.fragment_detail_hs1), DetailHs1ItemClickListener {
    private var _binding: FragmentDetailHs1Binding? = null
    private val binding get() = _binding!!

    private var HS1: DetailMenu? = null // JSON 데이터를 저장할 변수

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
        // TODO: test가 끝나면 변경 - fetchData -> setLayout 순
        setLayout()
    }

    // 네트워크 통신 -> 코루틴으로 제어
    private fun fetchData(view: View) {

        //TODO: Progressbar 생성
        //progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            HS1 = fetchDetailMenu() // Retrofit에서 데이터 가져오기

            if (HS1 != null) {
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

        // TODO: 서버에서 메뉴 받아옴 (HS1가 null일 시에도 처리)
        HS1 = dummy.dDummy

        setTab()
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
                    connectAdapter("월요일")
                }
            })
            csLocation.setOnClickListener {
                setLocationHyperLink()
            }
        }
    }

    private fun setTab() {
        with(binding) {
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("월요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("화요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("수요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("목요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("금요일"))
        }
        // init tab
        connectAdapter("월요일")
    }

    private fun clickTab(tab: TabLayout.Tab?) {
        if (tab == null) {
            Log.e("RecyclerView Tab Error", "Tab이 없음")
            return
        }
        when (tab.position) {
            0 -> connectAdapter("월요일")
            1 -> connectAdapter("화요일")
            2 -> connectAdapter("수요일")
            3 -> connectAdapter("목요일")
            4 -> connectAdapter("금요일")
            else -> throw IllegalArgumentException("Invalid button config: $tab")
        }
    }

    private fun connectAdapter(weekly: String) {
        binding.rvDetailHs1Menu.adapter =
            DetailHs1ListAdapter(HS1!!, weekly, this)
    }

    private fun setBackToHome() {
        binding.toolbarDetailHs1.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setLocationHyperLink() {
        // TODO: 위치 하이퍼링크
        Log.e("DetailHs1Fragment", "setLocationHyperLink")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}