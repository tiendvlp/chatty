package com.devlogs.chatty.datasource.common.restconfig

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChannelMainServerRestClientConfig {

    @POST("channel/newchannel")
    fun createChannel (@Body body: CreateChannelReq.ReqBody) : Response<Any>
    class CreateChannelReq {
        data class ReqBody (val channelData: ChannelData, val firstMessage: FirstMessage) {
            data class ChannelData (val memberEmails: List<String>)
            data class FirstMessage (val body: String)
        }
    }

}