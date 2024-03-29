package com.devlogs.chatty.datasource.common.restconfig

import com.devlogs.chatty.common.SEND_TEXT_MESSAGE_POST_REQ_WITHOUT_PARAMS
import com.devlogs.chatty.domain.datasource.mainserver.model.MessageMainServerModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageMainServerRestClientConfig {

    @POST("$SEND_TEXT_MESSAGE_POST_REQ_WITHOUT_PARAMS/{channelid}")
    suspend fun sendTextMessage (@Path("channelid") channelId: String, @Body body: SendTextMessageReqBody) : Response<MessageMainServerModel>
    data class SendTextMessageReqBody(val body: String, val identify: String?)
}