package com.devlogs.chatty.screen.authenticationscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.common.BackPressDispatcher
import com.devlogs.chatty.screen.common.BackPressListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity(), BackPressDispatcher {
    private var mBackPressListeners : HashSet<BackPressListener> = HashSet()
    @Inject
    lateinit var mNavigator : AuthenticationScreenNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_authentication)
        mNavigator.init(savedInstanceState)
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