package com.devlogs.chatty.common.di

import android.app.Activity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.authenticationscreen.AuthenticationScreenNavigator
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.ncapdevi.fragnav.FragNavController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun provideLayoutInflater (activity: Activity): LayoutInflater? {
        return LayoutInflater.from(activity)
    }

    @Provides
    fun provideFragmentManager (activity: Activity): FragmentManager {
        return (activity as FragmentActivity).supportFragmentManager
    }

    @Provides
    fun provideMvcViewFactory (layoutInflater: LayoutInflater): MvcViewFactory {
        return MvcViewFactory(layoutInflater)
    }

    @Provides
    fun provideAuthenticationScreenNavigator (fragmentManager: FragmentManager) : AuthenticationScreenNavigator = AuthenticationScreenNavigator(getMainFragNavController(fragmentManager))

    private fun getMainFragNavController (fragmentManager: FragmentManager) : FragNavController {
        return FragNavController(fragmentManager, R.id.frameAuthenticationContent)
    }
}