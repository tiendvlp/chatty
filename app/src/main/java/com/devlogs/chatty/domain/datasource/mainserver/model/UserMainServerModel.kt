package com.devlogs.chatty.domain.datasource.mainserver.model

import com.devlogs.chatty.common.helper.getUserAvatar
import com.devlogs.chatty.common.mapper.Mapper
import com.devlogs.chatty.domain.entity.AccountEntity
import com.devlogs.chatty.domain.entity.user.UserEntity

data class UserMainServerModel (
        val id: String,
        val email: String,
        val name: String,
        val avatar: String
)

fun Mapper.toUserEntity (user: UserMainServerModel) =
        UserEntity(
                id = user.id,
                email = user.email,
                name = user.name,
        )

fun Mapper.toAccountEntity (user: UserMainServerModel) =
        AccountEntity(
                id = user.id,
                email = user.email,
                name = user.name,
        )