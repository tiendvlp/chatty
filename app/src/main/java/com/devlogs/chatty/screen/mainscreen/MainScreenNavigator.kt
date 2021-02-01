package com.devlogs.chatty.screen.mainscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.devlogs.chatty.screen.mainscreen.account_screen.controller.AccountFragment
import com.devlogs.chatty.screen.mainscreen.mainscreen.controller.MainFragment
import com.ncapdevi.fragnav.FragNavController
import java.lang.IndexOutOfBoundsException

class MainScreenNavigator {
    private val mFragNavController: FragNavController
    private val mRootFragmentListener : FragNavController.RootFragmentListener = object : FragNavController.RootFragmentListener {
        override val numberOfRootFragments: Int
            get() = 1

        override fun getRootFragment(index: Int): Fragment {
            when (index) {
                FragNavController.TAB1 -> return MainFragment.getInstance()
                FragNavController.TAB2 -> return AccountFragment.getInstance()
                else -> throw IndexOutOfBoundsException("MainScreenNavigator only has 2 tabs but tab ${index}th was accessed")
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