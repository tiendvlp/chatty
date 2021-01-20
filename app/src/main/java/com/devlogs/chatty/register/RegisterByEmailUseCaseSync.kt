package com.devlogs.chatty.register

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.register.RegisterByEmailUseCaseSync.Result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterByEmailUseCaseSync {
    sealed class Result {
        object Success : Result()
        object UserAlreadyExistError : Result ()
        object GeneralError : Result ()
        object NetworkError : Result ()
    }

    private val mAuthServerApi : AuthServerApi

    @Inject
    constructor(authServerApi: AuthServerApi) {
        mAuthServerApi = authServerApi
    }

    suspend fun execute (email: String, password: String) = withContext(BackgroundDispatcher) {
        try {
            mAuthServerApi.register(email, password)
            Success
        } catch (e: NetworkErrorEntity) {
            NetworkError
        } catch (e: GeneralErrorEntity) {
            GeneralError
        } catch (e: DuplicateErrorEntity) {
             UserAlreadyExistError
        }
    }
}