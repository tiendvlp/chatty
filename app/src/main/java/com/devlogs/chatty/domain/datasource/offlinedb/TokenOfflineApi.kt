package com.devlogs.chatty.domain.datasource.offlinedb

/**
 * @IMPORTANT when the user is logged in, you have to save the refresh token first,
 * if it failed -> NO LOGIN OCCUR
 * if it success -> IT OK FOR THE ACCESS TOKEN TO BE MISSING */
interface TokenOfflineApi {
    suspend fun getAccessToken () : String
    suspend fun setAccessToken (accessToken: String)
    suspend fun getRefreshToken () : String
    suspend fun setRefreshToken (refreshToken: String)

    /**
     * There is no need for remove access token because it will expired in short time
     * But there is security need so we need to remove refresh Token, and it doesn't
     * make sense if you only remove refresh token and keep the access token
     * ==> REMOVE ALL TOKEN
     * */
    suspend fun clear ()
}