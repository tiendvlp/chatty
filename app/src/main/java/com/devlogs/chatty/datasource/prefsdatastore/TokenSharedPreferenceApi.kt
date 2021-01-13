package com.devlogs.chatty.datasource.prefsdatastore

import android.content.SharedPreferences
import com.devlogs.chatty.domain.datasource.offlinedb.TokenOfflineApi
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity.*
import java.util.*
import javax.inject.Inject

class TokenSharedPreferenceApi : TokenOfflineApi{
    private object PreferencesKeys {
        val ACCESS_TOKEN_KEY = "access_token"
        val ACCESS_TOKEN_EXPIRED_TIME ="access_token_expired_time"
        val REFRESH_TOKEN_KEY = "refresh_token"
        val REFRESH_TOKEN_EXPIRED_TIME = "refresh_token_expired_time"
    }

    private val mSharedPreference: SharedPreferences

    @Inject
    constructor(sharedPreferences: SharedPreferences) {
        mSharedPreference = sharedPreferences
    }

    override fun getAccessToken () : String {
        val accessToken : String =  mSharedPreference.getString(PreferencesKeys.ACCESS_TOKEN_KEY, null)
                ?:throw CannotGetAccessTokenErrorEntity("Couldn't found access token in your device")
        val accessTokenExpiredTime : Long = mSharedPreference.getLong(PreferencesKeys.ACCESS_TOKEN_EXPIRED_TIME, -1)

        if (accessTokenExpiredTime <= 0) {
            throw throw InvalidAccessTokenErrorEntity()
        }

        if (isExpired(accessTokenExpiredTime)) {
            throw InvalidAccessTokenErrorEntity()
        }

        return  accessToken
    }

    override fun setAccessToken (accessToken : String, expired: Long) {
        mSharedPreference.edit()
                .putString(PreferencesKeys.ACCESS_TOKEN_KEY, accessToken)
                .putLong(PreferencesKeys.ACCESS_TOKEN_EXPIRED_TIME, expired)
                .apply()
    }

    override fun getRefreshToken(): String {
        val refreshToken : String =  mSharedPreference.getString(PreferencesKeys.REFRESH_TOKEN_KEY, null)
                ?:throw CannotGetRefreshTokenErrorEntity("Couldn't found access token in your device")
        val refreshTokenExpiredTime : Long = mSharedPreference.getLong(PreferencesKeys.REFRESH_TOKEN_EXPIRED_TIME, -1)
        if (refreshTokenExpiredTime <= 0) {
            throw throw InvalidRefreshTokenErrorEntity()
        }

        if (isExpired(refreshTokenExpiredTime)) {
            throw InvalidRefreshTokenErrorEntity()
        }

        return  refreshToken
    }

    override fun setRefreshToken (refreshToken : String, expired: Long) {
        mSharedPreference.edit()
                .putString(PreferencesKeys.REFRESH_TOKEN_KEY, refreshToken)
                .putLong(PreferencesKeys.REFRESH_TOKEN_EXPIRED_TIME, expired)
                .apply()
    }


    override fun clear() {
      mSharedPreference.edit().clear().apply()
    }

    private fun isExpired(expiredTime: Long): Boolean {
        val currentTime = Date()
        return expiredTime <= currentTime.time
    }
}