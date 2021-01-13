package com.devlogs.chatty.domain.datasource.mainserver.model

data class MessageMainServerModel (
        val id: String,
        val type: String,
        val content: String,
        val createdDate: Long,
        val channelId : String)