package com.scm.sch_cafeteria_manager.ui.admin.master

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.scm.sch_cafeteria_manager.data.AdminData
import com.scm.sch_cafeteria_manager.databinding.FragmentMasterHs1Binding
import kotlinx.coroutines.launch

class MasterHs1WeekFragment: Fragment() {
    private var _binding: FragmentMasterHs1Binding? = null
    private val binding get() = _binding!!
    private val args: MasterHs1WeekFragmentArgs by navArgs()
    var data: AdminData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMasterHs1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 서버로부터 data 받기
        viewLifecycleOwner.lifecycleScope.launch {
            fetchData()
        }
    }

    private fun fetchData(){
        binding.progressbar.visibility = View.VISIBLE // UI 블로킹 시작
        //TODO
        binding.progressbar.visibility = View.GONE // 네트워크 완료 후 UI 다시 활성화
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //TODO: 변경점이 있는데 저장하지 않고 뒤로가기를 눌렀을 시 Toast Message
}