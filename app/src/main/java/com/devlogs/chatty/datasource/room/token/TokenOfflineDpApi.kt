package com.devlogs.chatty.datasource.room.token

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface TokenOfflineDpApi {
    fun getAccessToken () : Single<String>
    fun updateAccessToken(accessToken: String) : Completable
    fun updateRefreshToken(refreshToken: String) : Completable
    suspend fun getAccessToken2 () : String
}
