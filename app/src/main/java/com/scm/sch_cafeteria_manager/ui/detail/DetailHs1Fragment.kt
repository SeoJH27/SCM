package com.scm.sch_cafeteria_manager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.scm.sch_cafeteria_manager.databinding.FragmentDetailHs1Binding

class DetailHs1Fragment : Fragment() {
    private var _binding: FragmentDetailHs1Binding? = null
    private val binding get() = _binding!!

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

        setLayout()
    }

    private fun setLayout() {
        // TODO: 서버에서 메뉴 받아옴

        // TODO: 코드 정리

        with(binding) {
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("월요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("화요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("수요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("목요일"))
            tlDetailHs1.addTab(tlDetailHs1.newTab().setText("금요일"))

            tlDetailHs1.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                //TODO: 각 탭에 맞는 데이터를 세팅
                //when(tab.position){
                //}
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })
        }


        binding.editLocation.setOnClickListener {
            setLocationHyperLink()
        }
        binding.btnMapPin.setOnClickListener {
            setLocationHyperLink()
        }
        setBackToHome()

    }

    private fun setBackToHome() {
        binding.toolbarDetailHs1.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setLocationHyperLink() {
        // TODO: 위치 하이퍼링크
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}