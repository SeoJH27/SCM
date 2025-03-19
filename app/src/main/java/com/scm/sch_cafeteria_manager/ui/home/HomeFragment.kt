package com.scm.sch_cafeteria_manager.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.scm.sch_cafeteria_manager.databinding.FragmentHomeBinding
import com.scm.sch_cafeteria_manager.ui.login.LoginActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnHs1.setOnClickListener {
                navigateTo("hs1")
            }
//            btnHs2.setOnClickListener {
//                navigateTo("hs2")
//            }
            btnStaff.setOnClickListener {
                navigateTo("staff")
            }
//            btnStudentUnion.setOnClickListener {
//                navigateTo("student")
//            }
            icMap.setOnClickListener {
                navigateTo("todaymenu")
            }
            txtLogin.setOnClickListener{
                toLogin()
            }
        }
    }

    private fun toLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateTo(destination: String) {
        findNavController().navigate(navigateOtherFragment(destination))
    }

    private fun navigateOtherFragment(destination: String): NavDirections =
        when (destination) {
            "hs1" -> HomeFragmentDirections.homeToHs1()
            "hs2" -> HomeFragmentDirections.homeToHs2()
            "staff" -> HomeFragmentDirections.homeToStaff()
            "student" -> HomeFragmentDirections.homeToStudentUnion()
            "todaymenu" -> HomeFragmentDirections.homeToTodayMenu()
            else -> throw IllegalArgumentException("Invalid button config: $destination")
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}