package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class ChatBoxMvcView : BaseMvcView<ChatBoxMvcView.Listener> {

    interface Listener {

    }


    private val mainLayout: ConstraintLayout
    private val window: Window
    private val blurView: BlurView
    constructor(toolkit: UIToolkit, container: ViewGroup?) {
        this.window = toolkit.window
        setRootView(toolkit.layoutInflater.inflate(R.layout.layout_chatbox, container, false))
        mainLayout = findViewById(R.id.mainLayout)
        blurView = findViewById(R.id.blurView)
        blur()
    }

    private fun blur () {
        val radius = 3f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.getBackground()
        blurView.setupWith(decorView.findViewById(android.R.id.content))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(getContext()))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(false)
    }

}