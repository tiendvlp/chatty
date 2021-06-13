package com.devlogs.chatty.domain.datasource.local

import com.devlogs.chatty.domain.error.AuthenticationErrorEntity

/**
 * @IMPORTANT when the user is logged in, you have to save the refresh token first,
 * if it failed -> NO LOGIN OCCUR
 * if it success -> IT OK FOR THE ACCESS TOKEN TO BE MISSING */
interface TokenOfflineApi {
    /**
     * @throws AuthenticationErrorEntity.CannotGetAccessTokenErrorEntity
     * @throws AuthenticationErrorEntity.InvalidAccessTokenErrorEntity
     * */
     fun getAccessToken () : String

     fun setAccessToken (accessToken: String, expired: Long)

    /**
     * @throws AuthenticationErrorEntity.InvalidRefreshTokenErrorEntity
     * @throws AuthenticationErrorEntity.CannotGetRefreshTokenErrorEntity
     * */
     fun getRefreshToken () : String

     fun setRefreshToken (refreshToken: String, expired: Long)

    /**
     * There is no need for remove access token because it will expired in short time
     * But there is security need so we need to remove refresh Token, and it doesn't
     * make sense if you only remove refresh token and keep the access token
     * ==> REMOVE ALL TOKEN
     * */
     fun clear ()
    object ClearTokenResult
}