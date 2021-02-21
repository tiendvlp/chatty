package com.devlogs.chatty.datasource.common.restconfig

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserMainServerRestClientConfig {
    @POST("/newuser")
    suspend fun createNewUser (@Body reqBody: CreateNewUser.ReqBody) : Response<Any>
    class CreateNewUser {
        data class ReqBody(val name:String)
    }
}