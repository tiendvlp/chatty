package com.devlogs.chatty.repository.authentication

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface TokenRepository {
    fun getAccessToken () : Single<String>
    fun updateRefreshToken (refreshToken: String) : Completable
    fun updateAccessToken (accessToken: String) : Completable
}