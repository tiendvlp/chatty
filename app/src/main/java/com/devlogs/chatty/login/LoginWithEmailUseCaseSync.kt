package com.devlogs.chatty.login

import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi.*
import com.devlogs.chatty.domain.datasource.offlinedb.TokenOfflineApi
import com.devlogs.chatty.domain.error.CommonErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync.Result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginWithEmailUseCaseSync {

    sealed class Result {
        object Success : Result()
        object NetworkError : Result()
        object InvalidAccountError : Result()
        object GeneralError : Result()
    }

    private val mAuthRestApi : AuthServerApi
    private val mTokenOfflineApi: TokenOfflineApi

    @Inject
    constructor(authRestApi: AuthServerApi, tokenOfflineApi: TokenOfflineApi) {
        mAuthRestApi = authRestApi
        mTokenOfflineApi = tokenOfflineApi
    }

    suspend fun execute (email: String, password: String) : Result = withContext(Dispatchers.IO) {
        try {
            val loginResult = mAuthRestApi.loginByEmail(email, password)
            mTokenOfflineApi.setRefreshToken(loginResult.refreshToken.token, loginResult.refreshToken.expiredAt)
            mTokenOfflineApi.setAccessToken(loginResult.accessToken.token, loginResult.accessToken.expiredAt)
            Success
        } catch (e : NetworkErrorEntity) {
            NetworkError
        } catch (e: NotFoundErrorEntity) {
            InvalidAccountError
        } catch (e: GeneralErrorEntity) {
            GeneralError
        }
    }
}
