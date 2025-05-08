package com.scm.sch_cafeteria_manager.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NetworkMonitor {
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    fun registerNetworkCallback(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()

        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("NetworkMonitor", "인터넷 연결됨")
                _isConnected.postValue(true)
            }
            override fun onLost(network: Network) {
                Log.d("NetworkMonitor", "인터넷 끊김")
                _isConnected.postValue(false)
            }
        })
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }

}
