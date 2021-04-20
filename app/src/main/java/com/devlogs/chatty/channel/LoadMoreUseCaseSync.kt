package com.devlogs.chatty.channel

import android.provider.Telephony.Carriers.PORT
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi
import com.devlogs.chatty.domain.datasource.mapper.toChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * @Excute: when ever the user want to load more channel (the rule of load more is defined in LoadMorePolicy.kt)
 * There are 2 main step:
 * Fist: Load channel inside local db first
 * Check if the amount of loaded channels (by LoadMorePolicy.kt class ) if not:
 * Send the getPreviousChannelWithCount request to server (the count is LoadMorePolicy.requiredAmount - currentLoadedChannelAmount)
 * Save all channels that returned from getPreviousChannelWithCount req into localdb
 * */

class LoadMoreUseCaseSync {
    private val localDbApi: ChannelLocalDbApi
    private val mainServerApi: ChannelMainServerApi
    private val loadMorePolicy: LoadMoreChannelPolicy

    constructor(channelLocalDbApi: ChannelLocalDbApi, channelMainServerApi: ChannelMainServerApi) {
        this.localDbApi = channelLocalDbApi
        this.mainServerApi = channelMainServerApi
        this.loadMorePolicy = LoadMoreChannelPolicyImp()
    }

    suspend fun execute(since: Long) : List<ChannelEntity> = withContext(BackgroundDispatcher) {
        val channelCount = loadMorePolicy.getMaxNumberOfChannel()
        val result = ArrayList<ChannelEntity> ()
        result.addAll(localDbApi.getPreviousChannels(since, channelCount).map {
            it.toChannelEntity()
        })

        if (result.size < loadMorePolicy.getMaxNumberOfChannel()) {
            val currentLoadedMemberAvatar = 0
            val mainServerChannel = mainServerApi.getPreviousChannels(since, channelCount).map {
                it.members.forEach {
                    val url = URL("http://$PORT:3000/api/v1/user/avatar/download/${it.email}")
                    val urlConnection = url.openConnection()
                    
                    val inp = urlConnection.getInputStream()
                    inp.read()
                }

                    it.toChannelEntity()
            }
        }
        result
    }
}