package com.scm.sch_cafeteria_manager.ui.home

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scm.sch_cafeteria_manager.util.NetworkMonitor
import com.scm.sch_cafeteria_manager.util.NetworkMonitor.isNetworkConnected

abstract class BaseActivity: AppCompatActivity() {
    private var networkDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LiveData 통해 네트워크 상태 감지
        NetworkMonitor.isConnected.observe(this) { isConnected ->
            if (!isConnected) {
                showNetworkErrorDialog()
            } else {
                dismissNetworkDialog()
            }
        }
    }

    private fun showNetworkErrorDialog() {
        if (networkDialog?.isShowing == true) return

        networkDialog = AlertDialog.Builder(this)
            .setTitle("네트워크 연결 오류")
            .setMessage("인터넷이 연결되어 있지 않습니다.")
            .setCancelable(false)
            .setPositiveButton("다시 시도") { _, _ ->
                // 다시 체크
                if (isNetworkConnected(this)) {
                    dismissNetworkDialog()
                } else {
                    showNetworkErrorDialog()
                }
            }
            .setNegativeButton("앱 종료") { _, _ ->
                finishAffinity() // 모든 Activity 종료
            }
            .show()
    }

    private fun dismissNetworkDialog() {
        networkDialog?.dismiss()
        networkDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissNetworkDialog()
    }

}