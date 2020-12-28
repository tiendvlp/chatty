package com.devlogs.chatty.domain.datasource.mainserver

import com.devlogs.chatty.domain.datasource.mainserver.model.UserAvatarMainServerModel
import com.devlogs.chatty.domain.datasource.mainserver.model.UserMainServerModel
import com.devlogs.chatty.domain.error.ErrorEntity

interface UserMainServerApi {
    /**
     * @throws ErrorEntity.MissingDataError
     * @throws ErrorEntity.UnknownError
     * @throws ErrorEntity.NotFoundError
     * */
    suspend fun getUser (email: String): UserMainServerModel
    suspend fun createUser (name: String, userAvatarMainServerModel: UserAvatarMainServerModel, accessToken: String)
}