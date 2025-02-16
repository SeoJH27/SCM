package com.scm.sch_cafeteria_manager.ui.detail.studentunion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.scm.sch_cafeteria_manager.databinding.FragmentDetailStudentUnionBinding

class DetailStudentUnionFragment : Fragment() {
    private var _binding: FragmentDetailStudentUnionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        // TODO: 서버에서 메뉴 받아옴

    }

    private fun setLayout() {
        setBackToHome()
        binding.editLocation.setOnClickListener {
            setLocationHyperLink()
        }
        binding.btnMapPin.setOnClickListener {
            setLocationHyperLink()
        }
    }

    private fun setBackToHome() {
        binding.toolbarDetailStudentUnion.setNavigationOnClickListener {
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