package com.devlogs.chatty.mainserver.authentication

import com.devlogs.chatty.mainserver.common.restconfig.AuthMainRestClientConfig
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface AuthMainRestApi {
    fun loginByEmail (email : String, password : String) : Single<AuthMainRestClientConfig.LoginByEmailResBody>
    fun register (email : String, password: String) : Completable
}