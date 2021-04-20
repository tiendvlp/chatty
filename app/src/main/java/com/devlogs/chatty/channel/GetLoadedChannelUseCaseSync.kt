package com.devlogs.chatty.channel

import com.devlogs.chatty.channel.LocalChannelPolicy.Companion.ALLOW_ALL
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.domain.datasource.mapper.toChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLoadedChannelUseCaseSync {
    private var policy: LocalChannelPolicy
    private val channelLocalDbApi: ChannelLocalDbApi

    @Inject
    constructor(channelLocalDbApi: ChannelLocalDbApi) {
        this.policy = AllLocalChannelPolicyImp()
        this.channelLocalDbApi = channelLocalDbApi
    }

    suspend fun execute () : List<ChannelEntity> = withContext(BackgroundDispatcher) {
        if (policy.numberOfChannelAllowed() == ALLOW_ALL) {
            return@withContext channelLocalDbApi.getAllChannel().map {
                it.toChannelEntity()
            }
        }

        emptyList()
    }


    fun setPolicy (policy: LocalChannelPolicy) {
        this.policy = policy
    }
}