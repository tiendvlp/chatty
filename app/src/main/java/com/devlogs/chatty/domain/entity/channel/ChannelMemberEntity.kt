package com.devlogs.chatty.domain.entity.channel

import com.devlogs.chatty.domain.entity.user.UserAvatarEntity

data class ChannelMemberEntity (
    val id: String,
    val email : String,
    val name: String,
    val avatar : UserAvatarEntity
)
