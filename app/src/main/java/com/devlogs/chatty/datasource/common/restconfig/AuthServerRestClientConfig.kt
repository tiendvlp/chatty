package com.devlogs.chatty.datasource.common.restconfig

import com.devlogs.chatty.common.GENERATE_ACCESS_TOKEN_GET_REQ
import com.devlogs.chatty.common.LOGIN_POST_REQ
import com.devlogs.chatty.common.REGISTER_POST_REQ
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthServerRestClientConfig {

    @POST(REGISTER_POST_REQ)
    suspend fun register (@Body body: RegisterReqBody) : Response<Any>
    data class RegisterReqBody (
        val email : String,
        val password : String)

    @POST (LOGIN_POST_REQ)
    suspend fun loginByEmail (@Body body: LoginByEmailReqBody) : Response<LoginByEmailResBody>
    data class LoginByEmailReqBody (
        val email : String,
        val password : String)
    data class LoginByEmailResBody (
        val accessToken : TokenRes,
        val refreshToken: TokenRes) {
    }
    data class TokenRes (val token: String, val expiredAt: Long)

    @POST(GENERATE_ACCESS_TOKEN_GET_REQ)
    suspend fun generateNewAccessToken (@Body body: GenerateNewAccessTokenReq.ReqBody) : Response<GenerateNewAccessTokenReq.ResBody>
    class GenerateNewAccessTokenReq {
        data class ReqBody (val refreshToken: String)
        data class ResBody (
                val accessToken : TokenRes)
    }
}