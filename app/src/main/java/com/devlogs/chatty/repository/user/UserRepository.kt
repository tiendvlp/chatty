package com.devlogs.chatty.repository.user

import com.devlogs.chatty.entity.user.UserEntity
import io.reactivex.rxjava3.core.Single

interface UserRepository {
    fun getUser () : Single<UserEntity>
}