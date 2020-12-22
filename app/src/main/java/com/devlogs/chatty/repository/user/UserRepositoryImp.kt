package com.devlogs.chatty.repository.user

import com.devlogs.chatty.entity.user.UserEntity
import io.reactivex.rxjava3.core.Single

class UserRepositoryImp : UserRepository {
    override fun getUser(): Single<UserEntity> {
        TODO("Not yet implemented")
    }
}