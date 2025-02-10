package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.scm.sch_cafeteria_manager.databinding.FragmentTodayMenuBinding

class TodayMenuFragment:Fragment() {
    private var _binding:FragmentTodayMenuBinding? = null
    private val binding get() = _binding!!

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

    private fun setLayout() {
        //TODO: 코드 정리

        with(binding){
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("향설 1관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("향설 2관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("학생회관"))
            tlTodayMemu.addTab(tlTodayMemu.newTab().setText("교직원 식당"))

            tlTodayMemu.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    //TODO: 각 탭에 맞는 데이터를 세팅
                    //when(tab!!.position){

                    //}
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