package com.devlogs.chatty.domain.datasource.authserver

import com.devlogs.chatty.common.Either
import com.devlogs.chatty.common.None
import com.devlogs.chatty.domain.datasource.mainserver.model.LoginMainServerModel
import com.devlogs.chatty.domain.error.ErrorEntity

interface AuthServerApi {
    suspend fun loginByEmail (email : String, password : String) : Either<ErrorEntity, LoginMainServerModel>
    suspend fun register (email : String, password: String): Either<ErrorEntity, None>
    suspend fun generateNewAccessToken (refreshToken: String): String
}