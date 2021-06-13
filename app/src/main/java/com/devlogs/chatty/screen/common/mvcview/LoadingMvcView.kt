package com.devlogs.chatty.screen.common.mvcview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.devlogs.chatty.R

class LoadingMvcView : BaseMvcView <LoadingMvcView> {
    val DEFAULT_ERR_MESSAGE = "Oops ! \n Not working "

    interface Listener {

    }

    private var prgLoading: ProgressBar
    private var txtErrorMessage: TextView
    private var rootLayout: LinearLayout
    private var container: ViewGroup?

    constructor(inflater:LayoutInflater, container: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.layout_loading, container, false))
        this.container = container
        prgLoading = findViewById(R.id.prgLoading)
        txtErrorMessage = findViewById(R.id.txtErrorMessage)
        rootLayout = findViewById(R.id.rootLayout)
    }

    fun showError (errMessage: String?) {
        container?.visibility = View.VISIBLE
        prgLoading.visibility = View.GONE
        txtErrorMessage.visibility = View.VISIBLE
        if (!errMessage.isNullOrBlank()) {
            txtErrorMessage.text = errMessage
        } else {
            txtErrorMessage.text = DEFAULT_ERR_MESSAGE
        }
    }

    fun hideError () {
        container?.visibility = View.GONE
        prgLoading.visibility = View.GONE
        txtErrorMessage.visibility = View.GONE
        txtErrorMessage.text = ""
    }

    fun showLoading () {
        container?.visibility = View.VISIBLE
        txtErrorMessage.visibility = View.GONE
        prgLoading.visibility = View.VISIBLE
    }

    fun success () {
        container?.visibility = View.GONE
    }
}