package com.devlogs.chatty.domain.entity.message

sealed class MessageEntity {
    val id: String
    val channelId: String
    val type: String
    val senderEmail: String
    val createdDate: Long


    constructor(
        id: String,
        channelId: String,
        type: String,
        senderEmail: String,
        createdDate: Long
    ) {
        this.id = id
        this.channelId = channelId
        this.type = type
        this.senderEmail = senderEmail
        this.createdDate = createdDate
    }

    class TextMessageEntity : MessageEntity {
        val messageBody: String

        constructor(id : String, channelId: String, type: String, senderEmail: String, createdDate : Long, messageBody: String) : super(id,
            channelId,
            type,
            senderEmail,
            createdDate) {
            this.messageBody = messageBody
        }
    }

    class VideoMessageEntity : MessageEntity {
        val videoUrl: String

        constructor(id : String, channelId: String, type: String, senderEmail: String, createdDate : Long, videoUrl: String) : super(id,
            channelId,
            type,
            senderEmail,
            createdDate) {
            this.videoUrl = videoUrl
        }
    }
}


