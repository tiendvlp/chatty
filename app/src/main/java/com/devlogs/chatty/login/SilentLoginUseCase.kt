package com.devlogs.chatty.login

import com.devlogs.chatty.domain.datasource.local.TokenOfflineApi
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity.*
import com.devlogs.chatty.login.SilentLoginUseCase.Result.*
import javax.inject.Inject

class SilentLoginUseCase {
    sealed class Result {
        object Allow: Result()
        object NotAllow: Result()
    }

    private val mTokenApi : TokenOfflineApi

    @Inject
    constructor(tokenOfflineApi: TokenOfflineApi) {
        mTokenApi = tokenOfflineApi
    }

    fun execute () : Result {
        return try {
            mTokenApi.getRefreshToken()
            Allow
        } catch (e: CannotGetRefreshTokenErrorEntity) {
            NotAllow
        } catch (e: InvalidRefreshTokenErrorEntity) {
            NotAllow
        }
    }
}