package com.scm.sch_cafeteria_manager.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.concurrent.thread

class ShareViewModel: ViewModel() {
    var title: String = CafeteriaData.HYANGSEOL1.cfName
    fun changeTitle(string: String): MutableLiveData<String> {
        val changeTitle = MutableLiveData<String>()
        thread {
            title = string
            changeTitle.postValue(string)
        }
        return changeTitle
    }
}