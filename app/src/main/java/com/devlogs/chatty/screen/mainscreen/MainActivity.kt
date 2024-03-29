package com.devlogs.chatty.screen.mainscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devlogs.chatty.R
import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.BackPressDispatcher
import com.devlogs.chatty.screen.common.BackPressListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BackPressDispatcher {

    companion object {
        fun start(currentContext: Context) {
            currentContext.startActivity(Intent(currentContext, MainActivity::class.java))
        }
    }

    private var mBackPressListeners: HashSet<BackPressListener> = HashSet()

    @Inject lateinit var mNavigator: MainScreenNavigator

    private lateinit var mFrameContainer: FrameLayout
    private lateinit var rdgrMain: RadioGroup
    private lateinit var rbtnMainScreen: RadioButton
    private lateinit var rbtnAccountScreen: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.area_main)

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

    private fun addEvents() {
        rdgrMain.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                rbtnAccountScreen.id -> mNavigator.switchToAccountTab()
                rbtnMainScreen.id -> mNavigator.switchToMainTab()
            }
        }
    }

    override fun onBackPressed() {
        var isBackPressConsumed: Boolean = false

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
