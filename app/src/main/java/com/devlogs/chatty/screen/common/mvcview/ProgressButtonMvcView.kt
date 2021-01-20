package com.devlogs.chatty.screen.common.mvcview

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.devlogs.chatty.R

class ProgressButtonMvcView : BaseMvcView<ProgressButtonMvcView.Listener> {
    interface Listener {
        fun onBtnClicked ()
    }
    private val fade_out_anim : Animation = AlphaAnimation(0.6f, 1.0f).apply {
        duration = 340
        fillAfter = true
    }
    private val cardView : CardView;
    private val layout: LinearLayout
    private val progressbar: ProgressBar
    private val buttonText: TextView
    var isEnable: Boolean
        get() {
            return layout.isEnabled
        }
        set(value) {
            layout.isEnabled = value
        }
    private var text: String = "Progress Button"

    constructor(layoutInflater: LayoutInflater, container: ViewGroup, text: String) {
        setRootView(layoutInflater.inflate(R.layout.layout_progressbutton, container, false))
        this.text = text
        cardView = findViewById(R.id.cardView)
        layout = findViewById(R.id.mainLayout)
        progressbar = findViewById(R.id.prg)
        buttonText = findViewById(R.id.txt)
        buttonText.text = text
        progressbar.visibility = GONE

        layout.setOnTouchListener(createCustomOnTouchListener())
        layout.setOnClickListener {
            getListener().forEach {
                it.onBtnClicked()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createCustomOnTouchListener () : OnTouchListener = OnTouchListener {
            view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                view.alpha = 0.6f
                true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                view.alpha = 1.0f
                layout.startAnimation(fade_out_anim)
                layout.performClick()
                true
            }

            false
    }

    fun deactivate () {
        isEnable = false
        progressbar.visibility = GONE
        layout.setBackgroundColor(cardView.resources.getColor(R.color.dark2))
    }

    fun activate () {
        isEnable = true
        layout.setBackgroundColor(cardView.resources.getColor(R.color.main_color))
    }

    fun startProgress (loadingText: String = text, loadingColor: Int = cardView.resources.getColor(R.color.dark2)) {
        progressbar.visibility = VISIBLE
        layout.setBackgroundColor(loadingColor)
        buttonText.text = loadingText
    }

    fun stopProgress (normalColor: Int = cardView.resources.getColor(R.color.main_color)) {
        progressbar.visibility = GONE
        layout.setBackgroundColor(normalColor)
        buttonText.text = text
    }

    fun finishProgress (finishText: String = text, finishColor: Int = cardView.resources.getColor(R.color.green)) {
        progressbar.visibility = GONE
        layout.setBackgroundColor(finishColor)
        buttonText.text = finishText
    }

}