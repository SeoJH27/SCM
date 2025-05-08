package com.scm.sch_cafeteria_manager.ui.home

import android.app.Application
import com.scm.sch_cafeteria_manager.util.NetworkMonitor

class SCM: Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkMonitor.registerNetworkCallback(this)
    }
}