package com.devlogs.chatty.screen.common.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.view.isVisible

class ImageButton : androidx.appcompat.widget.AppCompatImageButton {

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

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (isVisible) {
          alpha = 1f
        } else {
            alpha = 0f
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(listener: OnTouchListener?) {
        val newOnTouchListener = OnTouchListener {
                view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                view.alpha = 0.6f
                mOnClickListener?.onClick(this)
                return@OnTouchListener true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                alpha = 1f;
                if (visibility == View.GONE || visibility == View.INVISIBLE) {
                    visibility = visibility
                    return@OnTouchListener true
                }
                startAnimation(fade_out_anim)
                Log.d("ImageButton", "VISIBILITY: ${visibility}")
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