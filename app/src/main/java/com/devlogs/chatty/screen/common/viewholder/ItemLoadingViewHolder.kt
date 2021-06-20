package com.devlogs.chatty.screen.common.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.devlogs.chatty.R

class ItemLoadingViewHolder : RecyclerView.ViewHolder {

    private val progressBar: ProgressBar

    constructor(inflater: LayoutInflater, parent: ViewGroup?) : super(inflater.inflate(R.layout.item_loading, parent, false)) {
        progressBar = itemView.findViewById(R.id.progressBar)
    }

    fun bind (isLoading: Boolean) {
        progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

}