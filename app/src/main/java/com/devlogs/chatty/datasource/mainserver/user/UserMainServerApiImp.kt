package com.devlogs.chatty.datasource.mainserver.user

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.devlogs.chatty.common.di.DaggerNamed
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.common.helper.simple
import com.devlogs.chatty.datasource.common.restconfig.UserMainServerRestClientConfig
import com.devlogs.chatty.datasource.common.restconfig.UserMainServerRestClientConfig.CreateNewUser
import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.UserAvatarMainServerModel
import com.devlogs.chatty.domain.datasource.mainserver.model.UserMainServerModel
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mainserver.GetUserByEmailQuery
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class UserMainServerApiImp : UserMainServerApi {
    private val mApolloClient : ApolloClient
    private val mMainServerClinet : Retrofit

    @Inject
    constructor(apolloClient: ApolloClient, @Named(DaggerNamed.Retrofit.MainServerRetrofit) retrofit: Retrofit) {
        mMainServerClinet = retrofit
        this.mApolloClient = apolloClient
    }

    override suspend fun getUser(email: String) : UserMainServerModel {
        try {
            val response = mApolloClient.query(GetUserByEmailQuery(email = email)).await()

            if (response.hasErrors()) {
                val currentError = response.errors!!.get(0).simple()
                if (currentError.code == 404) {
                    throw NotFoundErrorEntity("Can not find your user info")
                }

                throw GeneralErrorEntity("Internal Server error")
            }

            val result: GetUserByEmailQuery.GetUserByEmail = response.data?.getUserByEmail
                    ?: throw NotFoundErrorEntity("Can not find your user")
            if (result.avatar == null) {
                throw GeneralErrorEntity("Your user avatar is missing")
            }
            val avatar = createUserAvatar(result.avatar)

            normalLog("GetUserSuccess: Avatar: " + avatar.type)
            return UserMainServerModel(result.id, result.email, result.name, avatar)

        } catch (e: ApolloException) {
            throw NetworkErrorEntity(e.message ?: "")
        }
    }

    override suspend fun createUser(name: String, userAvatarMainServerModel: UserAvatarMainServerModel) {
        try {
            val reqBody: CreateNewUser.ReqBody = CreateNewUser.ReqBody(name, userAvatarMainServerModel)
            val client = mMainServerClinet.create(UserMainServerRestClientConfig::class.java)
            val response = client.createNewUser(reqBody)

            if (!response.isSuccessful) {
                throw GeneralErrorEntity(response.message())
            }

        } catch (e: HttpException) {
            throw NetworkErrorEntity(e.message())
        }
    }

    private fun createUserAvatar(avatarRaw: GetUserByEmailQuery.Avatar): UserAvatarMainServerModel {
        val contentJson = Gson().toJsonTree(avatarRaw.content)
        if (avatarRaw.type == "LOCAL") {
            val localAvatarContentJson = contentJson.asJsonObject
            return UserAvatarMainServerModel.LocalAvatar(
                    avatarRaw.type,
                    localAvatarContentJson.get("avatarName").asString,
                    localAvatarContentJson.getAsJsonArray("avatarColor").map { jsonElement -> jsonElement.asFloat })
        }
        throw GeneralErrorEntity("Your avatar is not supported")
    }
}