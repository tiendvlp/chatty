package com.devlogs.chatty.datasource.interceptor

import com.devlogs.chatty.config.LOCALHOST
import com.devlogs.chatty.datasource.common.restconfig.AuthServerRestClientConfig
import com.devlogs.chatty.domain.datasource.offlinedb.TokenOfflineApi
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import javax.inject.Inject


class AuthInterceptor : Interceptor {
    private val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
    private val tokenOfflineApi : TokenOfflineApi

    @Inject
    constructor (tokenOfflineApi: TokenOfflineApi) {
        this.tokenOfflineApi = tokenOfflineApi
    }

    override fun intercept(chain: Interceptor.Chain): Response  {
        val accessToken = getAccessTokenFromDb(chain)
        val authorizedRequest = chain.request().newBuilder()
                .addHeader("authorization", "Bearer $accessToken").build()
        return chain.proceed(authorizedRequest)
    }

    private fun getAccessTokenFromDb (chain: Interceptor.Chain) : String {
        try {
            val accessToken = tokenOfflineApi.getAccessToken()
            return accessToken
        } catch (e: InvalidAccessTokenErrorEntity) {
            return generateNewAccessToken(chain)
        } catch (e: CannotGetAccessTokenErrorEntity) {
            return generateNewAccessToken(chain)
        }
    }


    /**
     * @throws InvalidRefreshTokenErrorEntity to be simple, any exception when getting new refresh token will require user to login again
     * */
    private fun generateNewAccessToken (chain: Interceptor.Chain): String {
        var refreshToken = ""
        try {
            refreshToken = tokenOfflineApi.getRefreshToken()
        } catch (e: Exception) {
            throw InvalidRefreshTokenErrorEntity()
        }
        val reqBody: RequestBody = RequestBody.create(MediaType.get("application/json"), "{\"refreshToken\": \"$refreshToken\"}")

        val authReq: Request = Request.Builder()
                .url("http://$LOCALHOST:4000/auth/token/generateaccesstoken")
                .method("POST", reqBody)
                .build()
        val authRes = chain.proceed(authReq)

        if (authRes.code() == 200) {
            val data = Gson().fromJson(authRes.body()!!.string(), AuthServerRestClientConfig.GenerateNewAccessTokenReq.ResBody::class.java)
            tokenOfflineApi.setAccessToken(data.accessToken.token, data.accessToken.expiredAt)
            authRes.close()
            return data.accessToken.token
        }
        authRes.close()
        throw InvalidRefreshTokenErrorEntity()
    }


}