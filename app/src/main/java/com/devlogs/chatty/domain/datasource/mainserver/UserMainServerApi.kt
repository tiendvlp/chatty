package com.devlogs.chatty.domain.datasource.mainserver

import com.devlogs.chatty.domain.datasource.mainserver.model.UserMainServerModel
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity
import com.devlogs.chatty.domain.error.ErrorEntity

interface UserMainServerApi {
    /**
     * @throws CommonErrorEntity.NotFoundErrorEntity
     * @throws CommonErrorEntity.NetworkErrorEntity
     * @throws AuthenticationErrorEntity
     * */
    suspend fun getUser (email: String): UserMainServerModel

    /**
     * @throws CommonErrorEntity.NetworkErrorEntity
     * @throws CreateUserError.UserAlreadyExist
     * @throws AuthenticationErrorEntity
     * */
    suspend fun createUser (name: String)
    sealed class CreateUserError(message: String) : ErrorEntity(message) {
        object UserAlreadyExist: CreateUserError("Your user email is already exist")
    }

    suspend fun getMyAccount () : UserMainServerModel
}