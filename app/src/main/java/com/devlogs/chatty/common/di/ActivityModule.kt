package com.devlogs.chatty.common.di

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.devlogs.chatty.R
import com.devlogs.chatty.datasource.local.internalfilesystem.InternalResourceImp
import com.devlogs.chatty.domain.datasource.local.InternalResource
import com.devlogs.chatty.screen.authenticationscreen.AuthenticationScreenNavigator
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.MainScreenNavigator
import com.ncapdevi.fragnav.FragNavController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun provideLayoutInflater (activity: Activity): LayoutInflater {
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
    @ActivityScoped
    fun provideAuthenticationScreenNavigator (fragmentManager: FragmentManager) : AuthenticationScreenNavigator = AuthenticationScreenNavigator(getAuthenticationFragNavController(fragmentManager))

    @Provides
    @ActivityScoped
    fun provideMainScreenNavigator (fragmentManager: FragmentManager) : MainScreenNavigator = MainScreenNavigator(getMainFragNavController(fragmentManager))


    @Provides
    @ActivityScoped
    fun providePresentationStateManager () : PresentationStateManager {
        return PresentationStateManager()
    }

    private fun getAuthenticationFragNavController (fragmentManager: FragmentManager) : FragNavController {
        return FragNavController(fragmentManager, R.id.frameAuthenticationContent)
    }

    private fun getMainFragNavController (fragmentManager: FragmentManager) : FragNavController {
        return FragNavController(fragmentManager, R.id.mainLayoutContainer)
    }

    @ActivityScoped
    @Provides
    @Named(DaggerNamed.File.ExternalFileDir)
    fun provideExternalFileDir (activity: Activity) : File {
        return activity.externalCacheDir!!
    }

    @Provides
    fun provideInternalResource (internalResourceImp: InternalResourceImp) : InternalResource = internalResourceImp
}