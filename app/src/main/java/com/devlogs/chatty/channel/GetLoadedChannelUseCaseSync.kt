package com.devlogs.chatty.channel

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.domain.datasource.mapper.toChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLoadedChannelUseCaseSync {
    private val channelLocalDbApi: ChannelLocalDbApi

    @Inject
    constructor(channelLocalDbApi: ChannelLocalDbApi) {
        this.channelLocalDbApi = channelLocalDbApi
    }

    suspend fun execute () : List<ChannelEntity> = withContext(BackgroundDispatcher) {
        channelLocalDbApi.getAllChannel().map {
            it.toChannelEntity()
        }
    }
}