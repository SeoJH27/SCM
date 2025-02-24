package com.scm.sch_cafeteria_manager.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scm.sch_cafeteria_manager.databinding.ActivityAdminBinding
import com.scm.sch_cafeteria_manager.databinding.ActivityHomeBinding

class AdminActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}