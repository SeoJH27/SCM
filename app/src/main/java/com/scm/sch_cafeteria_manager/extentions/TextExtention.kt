package com.scm.sch_cafeteria_manager.extentions

fun String.replaceCommaToLinebreak(): String {
     return this.replace(",", "\n")
}

inline fun <reified T : Enum<T>> String.toEnumOrNull(): T? =
     enumValues<T>().firstOrNull { it.name == this }
