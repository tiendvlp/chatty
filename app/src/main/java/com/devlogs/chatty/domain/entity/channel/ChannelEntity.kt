package com.devlogs.chatty.domain.entity.channel

data class ChannelEntity (
    val id : String,
    val title : String,
    val admin : String,
    val status : ChannelStatusEntity,
    val members : List<ChannelMemberEntity>,
    val seen : List<String>,
    val createdDate : Long,
    val latestUpdate : Long) {

}
