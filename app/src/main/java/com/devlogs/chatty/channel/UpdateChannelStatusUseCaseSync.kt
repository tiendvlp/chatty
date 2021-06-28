package com.devlogs.chatty.channel

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.to
import com.devlogs.chatty.domain.datasource.mapper.toChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.NotFoundErrorEntity
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateChannelStatusUseCaseSync @Inject constructor( private val channelLocalDbApi: ChannelLocalDbApi) {

    sealed class Result {
        data class Success (val channel: ChannelEntity) : Result()
        object NotFound : Result()
    }

    suspend fun execute (channelId: String, newChannelStatus: ChannelStatusEntity, lastUpdate: Long) = withContext(BackgroundDispatcher) {
        try {
            val channelDb = channelLocalDbApi.getChannel(channelId)
            val channelEntity = channelDb.toChannelEntity()
            val newChannel = channelEntity.copy(status = newChannelStatus,latestUpdate = lastUpdate)
            normalLog("Save new channel: ${newChannel.latestUpdate}")
            channelLocalDbApi.addChannel(channelRO = newChannel.to())
            Result.Success(newChannel)
        } catch (e: NotFoundErrorEntity) {
            Result.NotFound
        }
    }

}