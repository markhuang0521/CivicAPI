package com.example.android.politicalpreparedness.election.adapter

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("liveData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data)
}


//@BindingAdapter("isVisible")
//fun setTextViewVisibility(view: TextView, elections: List<Election>?) {
//    if (elections != null && elections.isNotEmpty()) {
//        view.visibility = View.GONE
//    } else {
//        view.visibility = View.VISIBLE
//    }
//}

@BindingAdapter("isVisible")
fun setNoDataVisibility(view: TextView, visible: Boolean? = false) {
    if (visible == true) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}


@BindingAdapter("progressBarVisible")
fun setprogressBarVisibility(view: ProgressBar, visible: Boolean? = false) {
    if (visible == true) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}