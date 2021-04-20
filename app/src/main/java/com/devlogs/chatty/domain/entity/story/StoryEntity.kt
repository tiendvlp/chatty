package com.devlogs.chatty.domain.entity.story

data class StoryEntity (
    val id : String,
    val type : String,
    val owner : String,
    val upLoadedDate : Long,
    val outDated: Long,
    val channelId : String,
    val content : String
)
