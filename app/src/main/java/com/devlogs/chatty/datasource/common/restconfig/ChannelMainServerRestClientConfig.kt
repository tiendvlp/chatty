package com.devlogs.chatty.datasource.common.restconfig

import com.devlogs.chatty.common.CREATE_NEW_CHANNEL_POST_REQ
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChannelMainServerRestClientConfig {

    @POST(CREATE_NEW_CHANNEL_POST_REQ)
    fun createChannel (@Body body: CreateChannelReq.ReqBody) : Response<Any>
    class CreateChannelReq {
        data class ReqBody (val channelData: ChannelData, val firstMessage: FirstMessage) {
            data class ChannelData (val memberEmails: List<String>)
            data class FirstMessage (val body: String)
        }
    }

}