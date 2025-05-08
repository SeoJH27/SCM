package com.scm.sch_cafeteria_manager.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.scm.sch_cafeteria_manager.databinding.ActivityHomeBinding
import com.scm.sch_cafeteria_manager.util.fetchVersion
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName

        Log.d("HomeActivity", "versionName: $versionName")

        lifecycleScope.launch {
            val cerVersion = fetchVersion()?.message
            Log.d("HomeActivity", "cerVersion: $cerVersion")

            if (cerVersion != null) {
                if (cerVersion != versionName) {
                    checkVersion(this@HomeActivity)
                }
            }
        }
    }

    private fun checkVersion(context: Context) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("업데이트 안내")
        builder.setMessage("앱 버젼이 다릅니다. 보다 좋은 서비스를 위해 업데이트 해주세요.")
        builder.setPositiveButton(
            "업데이트"
        ) { _, _ ->
            val url2 = "https://play.google.com/store/apps/details?id=앱 패키지명"
            val i: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url2))
            startActivity(i)
            System.runFinalization()
            exitProcess(0)
        }
        builder.setNegativeButton(
            "나가기"
        ) { _, _ ->
            System.runFinalization()
            exitProcess(0)
        }
        builder.show()
    }
}