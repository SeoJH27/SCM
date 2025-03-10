package com.scm.sch_cafeteria_manager.extentions

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.TextView
import com.scm.sch_cafeteria_manager.R

fun TextView.setTimePickerDialog(context: Context) {
    val alarmHour = 0
    val alarmMinute = 0
    val timePickerDialog = TimePickerDialog(
        context,
        { view, hourOfDay, minute -> }, alarmHour, alarmMinute, false
    )
    timePickerDialog.show()
    Log.e("Admin1Hs1WeekFragment", "setTimePicker")
    this.text = String.format(resources.getString(R.string.timePickerText), alarmHour, alarmMinute)
}