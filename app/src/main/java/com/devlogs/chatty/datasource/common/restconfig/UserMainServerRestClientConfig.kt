package com.devlogs.chatty.datasource.common.restconfig

import com.devlogs.chatty.common.CREATE_NEW_USER_POST_REQ
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserMainServerRestClientConfig {
    @POST(CREATE_NEW_USER_POST_REQ)
    suspend fun createNewUser (@Body reqBody: CreateNewUser.ReqBody) : Response<Any>
    class CreateNewUser {
        data class ReqBody(val name:String)
    }
}