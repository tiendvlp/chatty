package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.devlogs.chatty.R
import com.devlogs.chatty.common.helper.convertDpToPx
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.compat.KeyboardMovementCompatListener
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class ChatBoxMvcView : BaseMvcView<ChatBoxMvcView.Listener>, KeyboardMovementCompatListener {

    interface Listener {

    }

    private val mainLayout: LinearLayout
    private val window: Window
    private val blurView: BlurView
    private val edtMessage: EditText
    private val buttonWraper: LinearLayout
    private val btnGallery: ImageButton
    private val btnExpand: ImageButton
    private val expandMenuAnimator : ValueAnimator

    constructor(toolkit: UIToolkit, container: ViewGroup?) {
        this.window = toolkit.window
        setRootView(toolkit.layoutInflater.inflate(R.layout.layout_chatbox, container, false))
        mainLayout = findViewById(R.id.mainLayout)
        blurView  = findViewById(R.id.blurView)
        buttonWraper = findViewById(R.id.buttonWraper)
        edtMessage = findViewById(R.id.edtMessage)
        btnExpand = findViewById(R.id.btnExpand)
        btnGallery = findViewById(R.id.btnGallery)
        expandMenuAnimator = ValueAnimator.ofFloat(convertDpToPx(getContext(), -78f), 0f).setDuration(300)
        blur()
        addEvents()
    }

    private var isAnimReversed = false

    private fun addEvents () {
        expandMenuAnimator.apply {
            addUpdateListener {
                buttonWraper.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(left = (it.animatedValue as Float).toInt())
                }
            }

            doOnStart {
                if (isAnimReversed) {
                    btnExpand.visibility = View.VISIBLE
                    btnGallery.visibility = View.GONE
                } else {
                    btnExpand.visibility = View.GONE
                    btnGallery.visibility = View.VISIBLE
                }
            }
        }

        edtMessage.setOnClickListener {
            isAnimReversed = true
            if (isAnimReversed) {
                btnExpand.visibility = View.VISIBLE
                btnGallery.visibility = View.GONE
            } else {
                btnExpand.visibility = View.GONE
                btnGallery.visibility = View.VISIBLE
            }
            expandMenuAnimator.reverse()
        }

        btnExpand.setOnClickListener {
            isAnimReversed = false
            if (isAnimReversed) {
                btnExpand.visibility = View.VISIBLE
                btnGallery.visibility = View.GONE
            } else {
                btnExpand.visibility = View.GONE
                btnGallery.visibility = View.VISIBLE
            }
            expandMenuAnimator.start()
        }
    }

    private fun blur () {
        val radius = 10f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        blurView.setupWith(decorView.findViewById(android.R.id.content))
            .setBlurAlgorithm(RenderScriptBlur(getContext()))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(false)
    }

    private var currentMargin: Float = 0f

    override fun onStart() {
        if (currentMargin == 0f) {
            // is opening
            btnGallery.visibility = View.GONE
            btnExpand.visibility = View.VISIBLE
        } else {
            btnGallery.visibility = View.VISIBLE
            btnExpand.visibility = View.GONE
        }
    }

    override fun onFinished() {
        normalLog("Keyboard stopped:")
    }

    override fun onProgress(delta: Int, distance: Int, maxDistance: Int) {
        if (maxDistance == 0) {
            return
        }
        buttonWraper.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            currentMargin = leftMargin - (delta * (convertDpToPx(getContext(), 117f)/maxDistance))

            if (currentMargin > 0) {
                currentMargin = 0f
            } else if (currentMargin < convertDpToPx(getContext(), -78f)) {
                currentMargin = convertDpToPx(getContext(), -78f)
            }
            updateMargins(left = (currentMargin+0.5).toInt())
        }
    }

}