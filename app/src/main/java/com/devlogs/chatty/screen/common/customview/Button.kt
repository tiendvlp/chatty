package com.devlogs.chatty.screen.common.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatButton

class Button : AppCompatButton {
    private val fade_out_anim : Animation = AlphaAnimation(0.6f, 1.0f).apply {
        duration = 200
        fillAfter = true
    }

    private var mOnClickListener : OnClickListener? = null

    constructor(context : Context) : super(context) {
        setOnTouchListener(null)
    }

    constructor(context : Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setOnTouchListener(null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(listener: OnTouchListener?) {

        val newOnTouchListener = OnTouchListener {
                view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                view.alpha = 0.6f
                return@OnTouchListener true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                view.alpha = 1.0f
                startAnimation(fade_out_anim)
                mOnClickListener?.onClick(this)
                return@OnTouchListener true
            }

            listener?.let {
                return@OnTouchListener it.onTouch(view, motionEvent)
            }

            false
        }

        super.setOnTouchListener(newOnTouchListener)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        this.mOnClickListener = listener
    }
}