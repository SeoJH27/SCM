package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.view.Menu
import com.scm.sch_cafeteria_manager.data.Meal

interface TodayMenuItemClickListener {
    fun onTodayMenuClick(meal: Meal){

    }
}