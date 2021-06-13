package com.devlogs.chatty.login

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.process.AccountLocalDbApi
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi
import com.devlogs.chatty.domain.datasource.local.TokenOfflineApi
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync.Result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
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
    private val accountLocalDbApi: AccountLocalDbApi

    @Inject
    constructor(authRestApi: AuthServerApi, tokenOfflineApi: TokenOfflineApi, accountLocalDbApi: AccountLocalDbApi) {
        mAuthRestApi = authRestApi
        mTokenOfflineApi = tokenOfflineApi
        Dispatchers.Main.immediate
        this.accountLocalDbApi = accountLocalDbApi
    }

    suspend fun execute (email: String, password: String) : Result = withContext(BackgroundDispatcher) {
        try {
            val loginResult = mAuthRestApi.loginByEmail(email, password)
            mTokenOfflineApi.setRefreshToken(loginResult.refreshToken.token, loginResult.refreshToken.expiredAt)
            if (!isActive) {mTokenOfflineApi.clear(); GeneralError}
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
