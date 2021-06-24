package com.devlogs.chatty.screen.common.compat

import android.annotation.SuppressLint
import android.os.CancellationSignal
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import androidx.core.graphics.Insets
import androidx.core.view.*
import com.devlogs.chatty.common.helper.normalLog

class ChattyCompat(private val view: View, private val container: View, private val window: Window) : ChattyCompatAction {
    private var animationController: WindowInsetsAnimationControllerCompat? = null

    private var posTop = 0
    private var posBottom = 0
    private var bottomSize = 0
    private var keyboardMovementCompatListener : KeyboardMovementCompatListener? = null
    private var keyboardHeight: Int = 0

    private val animationControlListener: WindowInsetsAnimationControlListenerCompat by lazy {
        object : WindowInsetsAnimationControlListenerCompat {

            override fun onReady(
                controller: WindowInsetsAnimationControllerCompat,
                types: Int
            ) {
                normalLog("Ready to launch")
                animationController = controller
            }

            override fun onFinished(controller: WindowInsetsAnimationControllerCompat) {
                normalLog("Finished")
                if (getWindowInset().isVisible(WindowInsetsCompat.Type.ime()) && keyboardHeight == 0) {
                    keyboardHeight = controller.currentInsets.bottom
                }
                animationController = null
            }

            override fun onCancelled(controller: WindowInsetsAnimationControllerCompat?) {
                normalLog("Canceled")
                animationController = null
            }
        }
    }

    fun setKeyboardMovementCompatListener (listener: KeyboardMovementCompatListener) {
        this.keyboardMovementCompatListener = listener
    }

    fun getWindowInset () =  WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets)

    override fun setupUiWindowInsets() {
        setUiWindowInsets()
    }


    override fun setupKeyboardAnimations() {
        animateKeyboardDisplay()
    }


    fun setUiWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(container) { v, insets ->
            if (posBottom == 0) {
                posTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
                bottomSize =insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                posBottom = bottomSize
            }

            v.updatePadding(top = posTop, bottom = posBottom)

            insets
        }
    }




    @SuppressLint("ClickableViewAccessibility")
    fun setKeyboardAnimArea (viewGroup: ViewGroup) {
        var previousY  = 0.0f
        var scrolledY = 0f
        var scrollToOpenKeyboard = false
        var visible = false
        var first = true;
        viewGroup.setOnTouchListener { v, event ->

            normalLog("OnTouch: ${event.action}")

            val y: Float = event.y
            normalLog("OnTouch: ${event.action}")

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    visible = getWindowInset().isVisible(
                        WindowInsetsCompat.Type.ime())
                    if (first) {
                        normalLog("First setup" )
                        first = false

                        if (visible) {
                            scrolledY =  getWindowInset().getInsets(
                                WindowInsetsCompat.Type.ime()).bottom.toFloat()
                        }

                        createWindowInsetsAnimation()
                    }



                    val dy: Float = previousY - y
                    scrollToOpenKeyboard = scrolledY < scrolledY + dy

                    scrolledY += dy

                    if (scrolledY < 0) {
                        scrolledY = 0f
                    }
                    normalLog("Action moved: ${scrolledY}")

                    animationController?.setInsetsAndAlpha(
                        Insets.of(0, 0, 0, scrolledY.toInt()),
                        1f,
                        0f
                    )
                }

                MotionEvent.ACTION_UP -> {
                    normalLog("UP action" )
                    first = true
                    scrolledY = 0f
                    animationController?.finish(scrollToOpenKeyboard)
                }

                MotionEvent.ACTION_CANCEL -> {
                    first = true
                    scrolledY = 0f
                    animationController?.finish(scrollToOpenKeyboard)
                }
            }
            previousY = event.y
            true
        }
    }

    private fun createWindowInsetsAnimation() {
        // only works on API >= 30
        WindowInsetsControllerCompat(this.window, view).controlWindowInsetsAnimation(
            WindowInsetsCompat.Type.ime(),
            -1,
            LinearInterpolator(),
            CancellationSignal(),
            animationControlListener
        )
    }

    fun animateKeyboardDisplay() {
        val cb = object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
            var previous = 0
            override fun onProgress(insets: WindowInsetsCompat,
                                    animations: MutableList<WindowInsetsAnimationCompat>): WindowInsetsCompat {
                posBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

                val delta = posBottom - previous

                previous = posBottom
                var distance = posBottom - bottomSize

                if (posBottom <= bottomSize) {
                    posBottom = bottomSize
                    distance = 0
                }

                container.updatePadding(top = posTop, bottom = posBottom)
                keyboardMovementCompatListener?.callback(delta, distance, keyboardHeight)
                return insets
            }
        }
        ViewCompat.setWindowInsetsAnimationCallback(view, cb)
    }

}