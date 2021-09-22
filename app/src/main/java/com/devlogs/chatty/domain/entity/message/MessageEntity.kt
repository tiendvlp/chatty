package com.devlogs.chatty.domain.entity.message

class MessageEntity {
    enum class Status {
        SENDING, FAILED, DONE
    }
    val id: String
    val channelId: String
    val type: String
    val content: String
    val senderEmail: String
    val createdDate: Long
    val status: Status

    constructor(
        id: String,
        channelId: String,
        type: String,
        content: String,
        senderEmail: String,
        createdDate: Long,
        status: Status
    ) {
        this.status = status
        this.content = content
        this.id = id
        this.channelId = channelId
        this.type = type
        this.senderEmail = senderEmail
        this.createdDate = createdDate
    }
}


