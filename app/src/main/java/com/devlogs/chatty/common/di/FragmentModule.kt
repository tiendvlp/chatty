package com.devlogs.chatty.common.di

import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class FragmentModule {
    @Provides
    @FragmentScoped
    fun providePresentationStateManager () : PresentationStateManager {
        return PresentationStateManager()
    }
}
