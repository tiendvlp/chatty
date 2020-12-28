package com.devlogs.chatty.datasource.common.restconfig

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface UserMainServerRestClientConfig {
    @POST("/newuser")
    suspend fun createNewUser (@HeaderMap header: Map<String, String>, @Body reqBody: CreateNewUser.ReqBody)
    class CreateNewUser {
        data class ReqBody(val name:String, val avatar: JsonObject)
    }
}