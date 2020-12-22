package com.devlogs.chatty.login

import android.util.Log
import com.devlogs.chatty.mainserver.authentication.AuthMainRestApi
import com.devlogs.chatty.repository.authentication.TokenRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

class LoginWithEmailUseCaseSync {
    private val mTokenRepository: TokenRepository
    private val mAuthMainRestApi : AuthMainRestApi

    @Inject
    constructor(tokenRepository: TokenRepository, authMainRestApi: AuthMainRestApi) {
        mTokenRepository = tokenRepository
        mAuthMainRestApi = authMainRestApi
    }
    // dan gminh tien

    fun execute (email: String, password: String) : Completable {
        return Completable.create { completableEmitter ->
            mAuthMainRestApi.loginByEmail(email, password).subscribeBy(
                onSuccess = {
                    Log.d("LoginWithSync", it.refreshToken)
                    completableEmitter.onComplete()
                },
                onError = {
                    completableEmitter.onError(it)
                }
            )
        }
    }
}
