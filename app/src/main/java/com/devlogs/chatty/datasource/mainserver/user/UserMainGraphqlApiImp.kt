package com.devlogs.chatty.datasource.mainserver.user

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.devlogs.chatty.common.di.DaggerNamed
import com.devlogs.chatty.common.helper.errorLog
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.UserAvatarMainServerModel
import com.devlogs.chatty.domain.datasource.mainserver.model.UserMainServerModel
import com.devlogs.chatty.domain.error.ErrorEntity
import com.devlogs.chatty.domain.error.ErrorHandler
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mainserver.GetUserByEmailQuery
import javax.inject.Inject
import javax.inject.Named

class UserMainGraphqlApiImp : UserMainServerApi {
    private val mApolloClient: ApolloClient
    private val mErrorHandler: ErrorHandler

    @Inject
    constructor(apolloClient: ApolloClient,@Named(DaggerNamed.ErrorHandler.GeneralErrorHandler) errorHandler: ErrorHandler) {
        mApolloClient = apolloClient
        mErrorHandler = errorHandler
    }

    /**
     * @throws ErrorEntity.MissingDataError
     * @throws ErrorEntity.UnknownError
     * @throws ErrorEntity.NotFoundError
     * */
    override suspend fun getUser(email: String): UserMainServerModel = withContext(Dispatchers.IO) {
        try {
            var response = mApolloClient.query(GetUserByEmailQuery(email = email))
            val result: GetUserByEmailQuery.GetUserByEmail = response.await().data?.getUserByEmail?: throw ErrorEntity.NotFoundError

            if (result.avatar == null) {
                throw  ErrorEntity.MissingDataError
            }
            val avatar = createUserAvatar(result.avatar)

            normalLog("GetUserSuccess: Avatar: " + avatar.type)
            return@withContext UserMainServerModel(result.id, result.email, result.name, avatar)

        } catch (e: ApolloException) {
            errorLog("Graphqlerror: " + e.message)
            throw ErrorEntity.UnknownError
        }
    }

    /**
     * @throws ErrorEntity.UnSupportedDataTypeError
     */
    private fun createUserAvatar(avatarRaw: GetUserByEmailQuery.Avatar): UserAvatarMainServerModel {
        val contentJson = Gson().toJsonTree(avatarRaw.content)
        if (avatarRaw.type.equals("LOCAL")) {
            val localAvatarContentJson = contentJson.asJsonObject
            return UserAvatarMainServerModel.LocalAvatar(
                    avatarRaw.type,
                    localAvatarContentJson.get("avatarName").asString,
                    localAvatarContentJson.getAsJsonArray("avatarColor").map { jsonElement -> jsonElement.asFloat })
        }
        throw ErrorEntity.UnSupportedDataTypeError

    }
}