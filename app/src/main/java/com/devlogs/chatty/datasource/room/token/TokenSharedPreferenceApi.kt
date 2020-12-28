package com.devlogs.chatty.datasource.room.token

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TokenSharedPreferenceApi : TokenOfflineDpApi {
    private val mSharedPreference : SharedPreferences
    private val ACCESSTOKEN_KEY = "ACCESSTOKEN"
    private val REFRESHTOKEN_KEY = "REFRESHTOKEN"


    @Inject
    constructor(sharedPreferences: SharedPreferences) {
        mSharedPreference = sharedPreferences
    }

    override fun getAccessToken() : Single<String> {
        return Single.create { emitter ->
            val accessToken =  mSharedPreference.getString(ACCESSTOKEN_KEY, "")
            if (accessToken.isNullOrBlank()) {
                emitter.onError(Error("Access token doesn't exist"))
            }
            emitter.onSuccess(accessToken)
        }
    }

    override fun updateAccessToken(accessToken: String) : Completable {
        return Completable.create { emitter ->
            mSharedPreference.edit().putString(ACCESSTOKEN_KEY, accessToken).commit()
            emitter.onComplete()
        }
    }

    override fun updateRefreshToken(refreshToken: String) : Completable {
        return Completable.create { emitter ->
            mSharedPreference.edit().putString(REFRESHTOKEN_KEY, refreshToken).commit()
            emitter.onComplete()
        }
    }

    override suspend fun getAccessToken2(): String {
        return withContext(Dispatchers.IO) {
            return@withContext ""
        }
    }
}