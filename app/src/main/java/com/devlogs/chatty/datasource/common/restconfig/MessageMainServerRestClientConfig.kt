package com.devlogs.chatty.datasource.common.restconfig

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageMainServerRestClientConfig {

    @POST("/message/sendtextmessage")
    suspend fun sendTextMessage (@Body body: SendTextMessageReqBody) : Response<Any>
    data class SendTextMessageReqBody (val body: String, val channelId: String)
}