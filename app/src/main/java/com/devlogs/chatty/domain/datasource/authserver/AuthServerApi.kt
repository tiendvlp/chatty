package com.devlogs.chatty.domain.datasource.authserver

import com.devlogs.chatty.domain.error.CommonErrorEntity
import com.devlogs.chatty.domain.error.ErrorEntity

interface AuthServerApi {

    /**
     * @throws CommonErrorEntity.NetworkErrorEntity
     * @throws CommonErrorEntity.GeneralErrorEntity
     * @throws CommonErrorEntity.NotFoundErrorEntity*/
    suspend fun loginByEmail (email : String, password : String) : LoginByEmailResult
    data class LoginByEmailResult (val accessToken: Token, val refreshToken: Token)  {
            data class Token (val token: String, val expiredAt: Long)
    }

    /**
    * @throws CommonErrorEntity.GeneralErrorEntity
    * @throws CommonErrorEntity.NetworkErrorEntity
    * @throws CommonErrorEntity.DuplicateErrorEntity
    * */
    suspend fun register (email : String, password: String)
}