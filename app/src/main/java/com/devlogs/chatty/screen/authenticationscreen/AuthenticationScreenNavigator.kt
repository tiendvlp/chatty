package com.devlogs.chatty.screen.authenticationscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.controller.LoginFragment
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.controller.RegisterFragment
import com.ncapdevi.fragnav.FragNavController
import java.lang.IndexOutOfBoundsException

class AuthenticationScreenNavigator {
    private val mFragNavController: FragNavController
    private val mRootFragmentListener : FragNavController.RootFragmentListener = object : FragNavController.RootFragmentListener {
        override val numberOfRootFragments: Int
            get() = 1

        override fun getRootFragment(index: Int): Fragment {
            when (index) {
                FragNavController.TAB1 -> return LoginFragment.getInstance()
                FragNavController.TAB2 -> return RegisterFragment.getInstance()
                else -> throw IndexOutOfBoundsException("AuthenticationScreenNavigator only has 2 tabs but tab ${index}th was accessed")
            }
        }
    }

    constructor(fragNavController: FragNavController) {
        mFragNavController = fragNavController
    }

    fun init (savedInstanceState : Bundle?) {
        mFragNavController.rootFragmentListener = mRootFragmentListener
        mFragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    fun onSavedInstanceState (outState : Bundle?) {
        mFragNavController.onSaveInstanceState(outState)
    }

    fun navigateToRegisterScreen () {
        mFragNavController.pushFragment(RegisterFragment.getInstance())
    }

    fun navigateBackToRoot () {
        mFragNavController.clearStack()
    }

    fun navigateBack () : Boolean{
        if (mFragNavController.isRootFragment) {
            return false
        }
        mFragNavController.popFragment()
        return true
    }
}