package com.devlogs.chatty.entity.channel

data class ChannelStatusEntity (
    val senderEmail : String,
    val description: ChannelStatusDescription
) {

    data class ChannelStatusDescription (
        val type : String,
        val content : Any
    )
}
