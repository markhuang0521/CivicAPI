package com.example.android.politicalpreparedness.utils

import java.text.DateFormat
import java.util.*

fun Date.formatToString(): String {
    val format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault())
    return format.format(this)
}

fun getToday(): Date {
    val calendar = Calendar.getInstance()
    return calendar.time
}

fun main() {


    val date = Calendar.getInstance().time
   println(date.formatToString())

}