package com.devlogs.chatty.domain.entity.channel

data class ChannelStatusEntity (
    val senderEmail : String,
    val content: String,
    val type: String
)
