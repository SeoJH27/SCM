package com.scm.sch_cafeteria_manager.extentions

import android.widget.TextView

fun String.replaceCommaToLinebreak(): String {
     return this.replace(",", "\n")
}
