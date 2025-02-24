package com.scm.sch_cafeteria_manager.ui.admin.admin3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.scm.sch_cafeteria_manager.databinding.FragmentAdminStaffBinding

class Admin3WeekFragment: Fragment() {
    private var _binding: FragmentAdminStaffBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBack()

    }

    fun setBack(){
        // TODO: 저장을 누르지 않았을 경우 경고 후 Back
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}