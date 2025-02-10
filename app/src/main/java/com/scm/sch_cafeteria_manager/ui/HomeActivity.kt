package com.scm.sch_cafeteria_manager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.databinding.ActivityHomeBinding

class HomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment


        //setNavigation()
    }

//    private fun setNavigation() {
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment
//
//        val navController = navHostFragment.navController
//
//    }

}