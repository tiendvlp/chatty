package com.devlogs.chatty.common.di

import com.devlogs.chatty.realtime.ChannelRealtime
import com.devlogs.chatty.realtime.MessageRealtime
import com.devlogs.chatty.realtime.StoryRealtime
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.socket.client.Socket
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class RealtimeModule {
    @Provides
    @Singleton
    fun provideMessageRealtime (socketInstance: Socket): MessageRealtime {
        return MessageRealtime(socketInstance)
    }

    @Provides
    @Singleton
    fun provideChannelRealtime (socketInstance: Socket) : ChannelRealtime {
        return ChannelRealtime(socketInstance)
    }

    @Provides
    @Singleton
    fun provideStoryRealtime (socketInstance: Socket) : StoryRealtime {
        return StoryRealtime(socketInstance)
    }
}