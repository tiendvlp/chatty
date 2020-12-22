package com.devlogs.chatty.entity.user

data class UserEntity (
      val id: String,
      val name: String,
      val email: String,
      val avatar: UserAvatarEntity )