package com.devlogs.chatty.common.di

import com.devlogs.chatty.channel.LoadMoreChannelPolicy
import com.devlogs.chatty.channel.LoadMoreChannelPolicyImp
import com.devlogs.chatty.chat.LoadMoreChatPolicy
import com.devlogs.chatty.chat.LoadMoreChatPolicyImp
import com.devlogs.chatty.datasource.authserver.authentication.AuthServerRestApiImp
import com.devlogs.chatty.datasource.local.internalfilesystem.InternalResourceImp
import com.devlogs.chatty.datasource.mainserver.channel.ChannelMainServerApiImp
import com.devlogs.chatty.datasource.mainserver.message.MessageMainServerApiImp
import com.devlogs.chatty.datasource.mainserver.user.UserMainServerApiImp
import com.devlogs.chatty.datasource.prefsdatastore.TokenSharedPreferenceApi
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi
import com.devlogs.chatty.domain.datasource.local.InternalResource
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.datasource.local.TokenOfflineApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
class DataDiModule {
    @Provides
    fun provideAuthMainRestApi (authMainRestApiImp: AuthServerRestApiImp) : AuthServerApi = authMainRestApiImp

    @Provides
    fun provideUserMainServerGraphqlApi (userMainServerApiImp: UserMainServerApiImp) : UserMainServerApi = userMainServerApiImp

    @Provides
    fun provideTokenOfflineApi (tokenSharedPreferenceApi: TokenSharedPreferenceApi) : TokenOfflineApi = tokenSharedPreferenceApi

    @Provides
    fun provideMessageMainServerApi (messageMainServerApiImp: MessageMainServerApiImp) : MessageMainServerApi = messageMainServerApiImp

    @Provides
    fun provideChannelMainServerApi (channelMainServerApi: ChannelMainServerApiImp) : ChannelMainServerApi = channelMainServerApi

    @Provides
    fun provideLoadChannelPolicy (loadMoreChannelPolicyImp: LoadMoreChannelPolicyImp) : LoadMoreChannelPolicy = loadMoreChannelPolicyImp

    @Provides
    fun provideLoadMessagePolicy (loadMoreMessagePolicy: LoadMoreChatPolicyImp) : LoadMoreChatPolicy = loadMoreMessagePolicy

//    @Provides
//    fun provideInternalResource (internalResourceImp: InternalResourceImp) : InternalResource = internalResourceImp
}