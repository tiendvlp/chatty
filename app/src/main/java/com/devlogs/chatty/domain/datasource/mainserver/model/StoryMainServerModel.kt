package com.devlogs.chatty.domain.datasource.mainserver.model

data class StoryMainServerModel (
    val id: String,
    val channelId: String,
    val content: String,
    val type: String,
    val owner: String,
    val uploadedDate: Long,
    val outDatedDate: Long,
        )
