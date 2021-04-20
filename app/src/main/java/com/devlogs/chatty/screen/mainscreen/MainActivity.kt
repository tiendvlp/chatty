package com.devlogs.chatty.screen.mainscreen

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.common.BackPressDispatcher
import com.devlogs.chatty.screen.common.BackPressListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BackPressDispatcher {
    private var mBackPressListeners : HashSet<BackPressListener> = HashSet()

    @Inject
    lateinit var mNavigator: MainScreenNavigator

    private lateinit var mFrameContainer: FrameLayout
    private lateinit var rdgrMain: RadioGroup
    private lateinit var rbtnMainScreen: RadioButton
    private lateinit var rbtnAccountScreen: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.messaging.subscribeToTopic("603236349deb10090ebd3a78")
            .addOnCompleteListener {
                Log.d("Register Room", "State: " + it.isSuccessful)
            }
        setContentView(R.layout.layout_main)
        mNavigator.init(savedInstanceState)
        rdgrMain = findViewById(R.id.rdgrMain)
        rbtnAccountScreen = findViewById(R.id.rbtnAccountScreen)
        rbtnMainScreen = findViewById(R.id.rbtnMainScreen)
        mFrameContainer = findViewById(R.id.mainLayoutContainer)

        addEvents()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mNavigator.onSavedInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun register(backPressListener: BackPressListener) {
        mBackPressListeners.add(backPressListener)
    }

    override fun unregister(backPressListener: BackPressListener) {
        mBackPressListeners.remove(backPressListener)
    }

    private fun addEvents () {
        rdgrMain.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                rbtnAccountScreen.id -> mNavigator.switchToAccountTab()
                rbtnMainScreen.id -> mNavigator.switchToMainTab()
            }
        }
    }

    override fun onBackPressed() {
        var isBackPressConsumed : Boolean = false

        mBackPressListeners.forEach { listener ->
            if (listener.onBackPress()) {
                isBackPressConsumed = true
            }
        }

        if (isBackPressConsumed) {
            return
        }

        if (!mNavigator.navigateBack()) {
            super.onBackPressed()
        }
    }
}