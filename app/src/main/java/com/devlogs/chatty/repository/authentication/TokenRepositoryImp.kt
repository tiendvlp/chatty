package com.devlogs.chatty.repository.authentication

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class TokenRepositoryImp : TokenRepository {

    override fun getAccessToken(): Single<String> {
        TODO("Not yet implemented")
    }

    override fun updateRefreshToken(refreshToken: String): Completable {
        TODO("Not yet implemented")
    }

    override fun updateAccessToken(accessToken: String): Completable {
        TODO("Not yet implemented")
    }
}