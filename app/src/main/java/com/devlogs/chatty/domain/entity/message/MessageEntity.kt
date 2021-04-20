package com.devlogs.chatty.domain.entity.message

class MessageEntity {
    val id: String
    val channelId: String
    val type: String
    val content: String
    val senderEmail: String
    val createdDate: Long

    constructor(
        id: String,
        channelId: String,
        type: String,
        content: String,
        senderEmail: String,
        createdDate: Long
    ) {
        this.content = content
        this.id = id
        this.channelId = channelId
        this.type = type
        this.senderEmail = senderEmail
        this.createdDate = createdDate
    }
}


