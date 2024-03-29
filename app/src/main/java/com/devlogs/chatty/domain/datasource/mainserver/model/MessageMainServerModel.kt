package com.devlogs.chatty.domain.datasource.mainserver.model

data class MessageMainServerModel (
        val _id: String,
        val type: String,
        val content: String,
        val createdDate: Long,
        val senderEmail: String,
        val channelId : String)