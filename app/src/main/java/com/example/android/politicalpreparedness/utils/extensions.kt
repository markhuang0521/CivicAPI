package com.example.android.politicalpreparedness.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

fun Fragment.setTitle(title: String? = "Political Preparedness") {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }
}

fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                bool
        )
    }
}

fun main() {


    val date = Calendar.getInstance().time
    println(date.formatToString())

}