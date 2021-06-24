package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.devlogs.chatty.R
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.compat.KeyboardMovementCompatListener
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class ChatBoxMvcView : BaseMvcView<ChatBoxMvcView.Listener>, KeyboardMovementCompatListener {

    interface Listener {

    }

    private val mainLayout: ConstraintLayout
    private val window: Window
    private val blurView: BlurView
    private val edtMessage: EditText

    constructor(toolkit: UIToolkit, container: ViewGroup?) {
        this.window = toolkit.window
        setRootView(toolkit.layoutInflater.inflate(R.layout.layout_chatbox, container, false))
        mainLayout = findViewById(R.id.mainLayout)
        blurView = findViewById(R.id.blurView)
        edtMessage = findViewById(R.id.edtMessage)
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

    override fun callback(delta: Int, distance: Int, maxDistance: Int) {
        edtMessage.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            normalLog("first Left: $leftMargin")
            var max : Float = maxDistance.toFloat()
            if (maxDistance == 0) {
                max = 700f
            }

            var a = leftMargin - (delta * (360/max))
//            if (distance == 0) {
//                a = 120f
//            } else {
//                a = 747f/distance + 120
//            }
            normalLog("callback $a")
            updateMargins(
                left =  (a+0.5f).toInt())
        }
    }


}