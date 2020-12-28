package com.devlogs.chatty.login

import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginWithEmailUseCaseSync {

    sealed class Result {
        object Success : Result()
        object GeneralError : Result()
    }

    private val mAuthRestApi : AuthServerApi

    @Inject
    constructor(authRestApi: AuthServerApi) {
        mAuthRestApi = authRestApi
    }

    suspend fun execute (email: String, password: String) : Result = withContext(Dispatchers.IO) {
        val loginResponse = mAuthRestApi.loginByEmail(email, password)
        if (loginResponse.isLeft) {
           return@withContext  Result.GeneralError
        }

        return@withContext Result.Success
    }
}
