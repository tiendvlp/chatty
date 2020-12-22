package com.devlogs.chatty.mainserver.common.restconfig

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthMainRestClientConfig {

    @POST("auth/register")
    fun register (@Body body: RegisterReqBody) : Call<Any>
    data class RegisterReqBody (
        val email : String,
        val password : String)

    @POST ("auth/login")
    fun loginByEmail (@Body body: LoginByEmailReqBody) : Call<LoginByEmailResBody>
    data class LoginByEmailReqBody (
        val email : String,
        val password : String)
    data class LoginByEmailResBody (
        val accessToken : String,
        val refreshToken: String)
}