package com.devlogs.chatty.screen.common.compat

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.CancellationSignal
import android.preference.PreferenceManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import androidx.core.graphics.Insets
import androidx.core.view.*
import com.devlogs.chatty.common.helper.normalLog

class ChattyCompat constructor(private val view: View, private val container: View, private val window: Window) : ChattyCompatAction {
    private var animationController: WindowInsetsAnimationControllerCompat? = null

    private var posTop = 0
    private var posBottom = 0
    private var bottomSize = 0
    private var keyboardMovementCompatListener : KeyboardMovementCompatListener? = null
    private var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)

    private var keyboardHeight: Int = sharedPreferences.getInt("KeyboardHeight",-1); set(value) {
        sharedPreferences.edit().putInt("KeyboardHeight", value).apply()
        field = value
    }

    private val animationControlListener: WindowInsetsAnimationControlListenerCompat by lazy {
        object : WindowInsetsAnimationControlListenerCompat {

            override fun onReady(
                controller: WindowInsetsAnimationControllerCompat,
                types: Int
            ) {
                normalLog("ON ANLOL")
                animationController = controller
            }

            override fun onFinished(controller: WindowInsetsAnimationControllerCompat) {
                animationController = null
            }

            override fun onCancelled(controller: WindowInsetsAnimationControllerCompat?) {
                animationController = null
            }
        }
    }

    private fun isKeyboardVisible () = getWindowInset().isVisible(WindowInsetsCompat.Type.ime())

    fun setKeyboardMovementCompatListener (listener: KeyboardMovementCompatListener) {
        this.keyboardMovementCompatListener = listener
    }

    private fun getWindowInset () =  WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets)

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
            normalLog("OnApply")
            if (isKeyboardVisible() && keyboardHeight == -1) {
                keyboardHeight = getWindowInset().getInsets(WindowInsetsCompat.Type.ime()).bottom
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
            val y: Float = event.y
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    visible = getWindowInset().isVisible(
                        WindowInsetsCompat.Type.ime())
                    if (first) {
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

                    animationController?.setInsetsAndAlpha(
                        Insets.of(0, 0, 0, scrolledY.toInt()),
                        1f,
                        0f
                    )
                }

                MotionEvent.ACTION_UP -> {
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
            var delta = 0
            var distance = 0
            override fun onStart(
                animation: WindowInsetsAnimationCompat,
                bounds: WindowInsetsAnimationCompat.BoundsCompat
            ): WindowInsetsAnimationCompat.BoundsCompat {
                keyboardMovementCompatListener?.onStart()
                return super.onStart(animation, bounds)
            }

            override fun onEnd(animation: WindowInsetsAnimationCompat) {
                keyboardMovementCompatListener?.onFinished()
                super.onEnd(animation)
            }
            override fun onProgress(insets: WindowInsetsCompat,
                                    animations: MutableList<WindowInsetsAnimationCompat>): WindowInsetsCompat {
                posBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

                delta = posBottom - previous
                previous = posBottom

                distance = posBottom - bottomSize

                if (posBottom <= bottomSize) {
                    posBottom = bottomSize
                    distance = 0
                }

                if ((delta == 0)) {
                    return insets
                }

                keyboardMovementCompatListener?.onProgress(delta, distance, keyboardHeight)
                container.updatePadding(top = posTop, bottom = posBottom)
                return insets
            }
        }
        ViewCompat.setWindowInsetsAnimationCallback(view, cb)
    }

}