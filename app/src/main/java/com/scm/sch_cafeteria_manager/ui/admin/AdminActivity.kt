package com.scm.sch_cafeteria_manager.ui.admin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.ShareViewModel
import com.scm.sch_cafeteria_manager.databinding.ActivityAdminBinding
import com.scm.sch_cafeteria_manager.databinding.ActivityLoginBinding
import com.scm.sch_cafeteria_manager.ui.home.BaseActivity

class AdminActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

// 권한
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PackageManager.PERMISSION_GRANTED) {
//            if (!allPermissionsGranted()) {
//                Toast.makeText(this,
//                    "Permissions not granted by the user.",
//                    Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }