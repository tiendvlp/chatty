package com.devlogs.chatty.screen.common.mvcview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.devlogs.chatty.R

class CheckingInputMvcView : BaseMvcView<CheckingInputMvcView.Listener> {
    interface Listener {

    }

    var visibility: Int
    get() {
        return mainLayout.visibility
    }
    set (value) {
        mainLayout.visibility = value
    }

    private val txtContent : TextView
    private val progress : ProgressBar
    private val mainLayout : LinearLayout
    private val imgChecked: ImageView
    private val imgWarning: ImageView

    constructor(layoutInflater: LayoutInflater, container: ViewGroup?, text: String) {
        setRootView(layoutInflater.inflate(R.layout.layout_checking, container, false))
        txtContent = findViewById(R.id.txtContent)
        progress = findViewById(R.id.prg)
        mainLayout = findViewById(R.id.main)
        imgChecked = findViewById(R.id.imgChecked)
        imgWarning = findViewById(R.id.imgWarning)
        txtContent.text = text
    }

    fun checking (text: String, color: Int = Color.WHITE) {
        txtContent.text = text
        txtContent.setTextColor(color)
        progress.visibility = View.VISIBLE
        imgChecked.visibility = View.GONE
        imgWarning.visibility = View.GONE
        txtContent.visibility = View.VISIBLE
    }

    fun valid (text: String, color: Int = mainLayout.resources.getColor(R.color.green)) {
        txtContent.text = text
        txtContent.setTextColor(color)
        progress.visibility = View.GONE
        imgWarning.visibility = View.GONE
        imgChecked.visibility = View.VISIBLE
        txtContent.visibility = View.VISIBLE
    }

    fun inValid (text: String, color: Int = mainLayout.resources.getColor(R.color.light_red)) {
        txtContent.text = text
        txtContent.setTextColor(color)
        progress.visibility = View.GONE
        imgWarning.visibility = View.VISIBLE
        imgChecked.visibility = View.GONE
        txtContent.visibility = View.VISIBLE
    }
}