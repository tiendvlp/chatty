package com.devlogs.chatty.screen.mainscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.devlogs.chatty.screen.mainscreen.account_screen.controller.AccountFragment
import com.devlogs.chatty.screen.mainscreen.channelscreen.controller.ChannelFragment
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavController.Companion.TAB1
import com.ncapdevi.fragnav.FragNavController.Companion.TAB2
import java.lang.IndexOutOfBoundsException

class MainScreenNavigator {
    private val mFragNavController: FragNavController
    private val mRootFragmentListener : FragNavController.RootFragmentListener = object : FragNavController.RootFragmentListener {
        override val numberOfRootFragments: Int
            get() = 2
    private lateinit var channelFragment: ChannelFragment
    private lateinit var userFragment: AccountFragment

        override fun getRootFragment(index: Int): Fragment {
            return when (index) {
                TAB1 -> {
                    ChannelFragment.getInstance()
                }
                TAB2 -> {
                    AccountFragment.getInstance()
                }
                else -> throw IndexOutOfBoundsException("MainScreenNavigator only has 2 tabs but tab ${index}th was accessed")
            }
        }
    }

    constructor(fragNavController: FragNavController) {
        mFragNavController = fragNavController
    }

    fun init (savedInstanceState : Bundle?) {
        mFragNavController.rootFragmentListener = mRootFragmentListener
        mFragNavController.initialize(TAB1, savedInstanceState)
    }

    fun onSavedInstanceState (outState : Bundle?) {
        mFragNavController.onSaveInstanceState(outState)
    }

    fun navigateBackToRoot () {
        mFragNavController.clearStack()
    }

    fun switchToAccountTab () {
        mFragNavController.switchTab(TAB2)
    }

    fun switchToMainTab () {
        mFragNavController.switchTab(TAB1)
    }

    fun navigateBack () : Boolean{
        if (mFragNavController.isRootFragment) {
            return false
        }
        mFragNavController.popFragment()
        return true
    }
}