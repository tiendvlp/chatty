package com.devlogs.chatty.domain.datasource.mainserver

import com.devlogs.chatty.domain.datasource.mainserver.model.ChannelMainServerModel
import com.devlogs.chatty.domain.error.CommonErrorEntity

interface ChannelMainServerApi {
    /**
     * @throws CommonErrorEntity.NotFoundErrorEntity
     * @throws CommonErrorEntity.NetworkErrorEntity
     * @throws CommonErrorEntity.UnAuthorizedErrorEntity
     * */
    suspend fun getPreviousChannels (lastUpdate: Long, count: Int) : List<ChannelMainServerModel>
    /**
     * @throws CommonErrorEntity.NotFoundErrorEntity
     * @throws CommonErrorEntity.NetworkErrorEntity
     * @throws CommonErrorEntity.UnAuthorizedErrorEntity
     * */
    suspend fun getChannelsOverPeriodOfTime (from: Long, to: Long) : List<ChannelMainServerModel>
}