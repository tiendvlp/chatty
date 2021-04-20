package com.devlogs.chatty.domain.datasource.mainserver

import com.devlogs.chatty.domain.datasource.mainserver.model.MessageMainServerModel
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity

interface MessageMainServerApi {
    /**
    * @throws AuthenticationErrorEntity
     * @throws CommonErrorEntity.GeneralErrorEntity
     * @throws CommonErrorEntity.NetworkErrorEntity
    * */
    suspend  fun sendTextMessage (messageBody: String, channelId: String);
    /**
     * @throws AuthenticationErrorEntity
     * @throws CommonErrorEntity.GeneralErrorEntity
     * @throws CommonErrorEntity.NetworkErrorEntity
     * */
    suspend fun getPreviousChannelMessages (channelId: String,since: Long, count: Int) : List<MessageMainServerModel>;

    suspend fun getChannelMessagesOverPeriodOfTime (channelId: String, from: Long, to: Long): List<MessageMainServerModel>
}