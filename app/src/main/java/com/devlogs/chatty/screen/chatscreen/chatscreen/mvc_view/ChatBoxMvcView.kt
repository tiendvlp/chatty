package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.animation.*
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnStart
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.doOnTextChanged
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
    private val btnSubmit: ImageButton
    private val expandMenuAnimator : ValueAnimator
    private val layoutEdt: LinearLayout

    private val animBtnSubmitScaleDown : ValueAnimator
    private val animBtnSubmitScaleUp : ValueAnimator
    private val animBtnSubmit : AnimatorSet

    private val drawableLike : Drawable
    private val drawableSend : Drawable
    private var currentDrawable : Drawable

    constructor (toolkit: UIToolkit, container: ViewGroup?) {
        this.window = toolkit.window
        setRootView(toolkit.layoutInflater.inflate(R.layout.layout_chatbox, container, false))
        mainLayout = findViewById(R.id.mainLayout)
        blurView  = findViewById(R.id.blurView)
        buttonWraper = findViewById(R.id.buttonWraper)
        edtMessage = findViewById(R.id.edtMessage)
        btnExpand = findViewById(R.id.btnExpand)
        layoutEdt = findViewById(R.id.layoutEdt)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnGallery = findViewById(R.id.btnGallery)

        drawableLike = getContext().resources.getDrawable(R.drawable.icon_like)
        drawableSend = getContext().resources.getDrawable(R.drawable.icon_send)
        currentDrawable = drawableLike
        btnSubmit.setImageDrawable(currentDrawable)
        expandMenuAnimator = ValueAnimator.ofFloat(convertDpToPx(getContext(), -78f), 0f).setDuration(150)

        animBtnSubmitScaleDown = ValueAnimator.ofFloat(1f, 0.6f).setDuration(80).apply {

            addUpdateListener {
                btnSubmit.scaleX = (animatedValue as Float)
                btnSubmit.scaleY = (animatedValue as Float) * -1
            }

            doOnCancel {
                btnSubmit.scaleY = 1f
                btnSubmit.scaleX = 1f
            }

        }
        animBtnSubmitScaleUp = ValueAnimator.ofFloat(0.6f, 1f).setDuration(80).apply {

            addUpdateListener {
                btnSubmit.scaleX = (animatedValue as Float)
                btnSubmit.scaleY = (animatedValue as Float) * -1
            }

            doOnStart {
                    btnSubmit.setImageDrawable(currentDrawable)
            }

            doOnCancel {
                btnSubmit.scaleY = 1f
                btnSubmit.scaleX = 1f
            }

        }
        animBtnSubmit = AnimatorSet()
        animBtnSubmit.playSequentially(animBtnSubmitScaleDown, animBtnSubmitScaleUp)

        blur()
        addEvents()
    }

    private var isAnimReversed = false

    private fun showButtonLike () {
        if (currentDrawable == drawableLike) {
            return
        }
        if (animBtnSubmit.isStarted && currentDrawable != drawableLike) {
            animBtnSubmitScaleDown.cancel()
        }
        if (!animBtnSubmit.isStarted) {
            currentDrawable = drawableLike
            animBtnSubmit.start()
        }
    }

    private fun showButtonSend () {
        if (currentDrawable == drawableSend) {
            return
        }
        if (animBtnSubmit.isRunning && currentDrawable != drawableSend) {
            animBtnSubmitScaleDown.cancel()
        }
        if (!animBtnSubmit.isRunning) {
            currentDrawable = drawableSend
            animBtnSubmit.start()
        }
    }

    private fun addEvents () {
        expandMenuAnimator.apply {
            setCurrentFraction(0.5f)
            addUpdateListener {
                buttonWraper.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(left = (it.animatedValue as Float).toInt())
                }
            }

            doOnStart {
                mainLayout.layoutTransition.disableTransitionType(LayoutTransition.CHANGING)
                if (isAnimReversed) {
                    btnExpand.visibility = View.VISIBLE
                    btnGallery.visibility = View.INVISIBLE
                } else {
                    btnExpand.visibility = View.INVISIBLE
                    btnGallery.visibility = View.VISIBLE
                }
            }
        }

        edtMessage.doOnTextChanged { text, start, before, count ->
            showButtonSend()
//            if (!mainLayout.layoutTransition.isTransitionTypeEnabled(LayoutTransition.CHANGING)) {
//                mainLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
//            }
            if (!isAnimReversed) {
                isAnimReversed = true
                buttonWraper.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(left = convertDpToPx(getContext(), -78f).toInt())
                }
                expandMenuAnimator.reverse()
            }
        }

        edtMessage.setOnClickListener {
            if (edtMessage.length() > 0) {
                showButtonSend()
            }
            if (!isAnimReversed) {
                isAnimReversed = true
                expandMenuAnimator.reverse()
                buttonWraper.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(left = 0)
                }
            }
        }

        btnGallery.setOnClickListener {
            normalLog("BtnGallery")
        }

        btnExpand.setOnClickListener {
            normalLog("BtnExpand")
            if (isAnimReversed) {
                isAnimReversed = false
                expandMenuAnimator.start()
            }
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
        mainLayout.layoutTransition.disableTransitionType(LayoutTransition.CHANGING)
        if (currentMargin == 0f) {
            // is opening
            btnGallery.visibility = View.GONE
            btnExpand.visibility = View.VISIBLE
            isAnimReversed = true
        } else {
            btnGallery.visibility = View.VISIBLE
            btnExpand.visibility = View.GONE
            isAnimReversed = false
        }
    }

    override fun onFinished() {
        if (currentMargin == 0f) {
            // is closed
                    showButtonLike()
        }
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