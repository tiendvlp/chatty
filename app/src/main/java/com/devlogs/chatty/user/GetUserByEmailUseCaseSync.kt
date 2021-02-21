package com.devlogs.chatty.user

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.UserAvatarMainServerModel
import com.devlogs.chatty.domain.entity.user.UserEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.user.GetUserByEmailUseCaseSync.Result.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserByEmailUseCaseSync {
    sealed class Result {
        data class Success(val userEntity: UserEntity) : Result()
        object UserNotFoundError : Result()
        object NetworkError : Result()
        object GeneralError : Result ()
    }

    private val mUserMainServerApi : UserMainServerApi

    @Inject
    constructor(userMainServerApi: UserMainServerApi) {
        mUserMainServerApi = userMainServerApi
    }

    suspend fun execute (userEmail: String) : Result = withContext(BackgroundDispatcher) {
        try {
            val getUserResult = mUserMainServerApi.getUser(userEmail)
            Success(UserEntity(getUserResult.id, getUserResult.name, getUserResult.email, getUserAvatarEntity(getUserResult.avatar)))
        } catch (e: GeneralErrorEntity) {
            GeneralError
        } catch (e: NotFoundErrorEntity) {
            UserNotFoundError
        } catch (e: NetworkErrorEntity) {
            NetworkError
        }
    }

    private fun getUserAvatarEntity (from: UserAvatarMainServerModel) : UserAvatarEntity {
        if (from is UserAvatarMainServerModel.LocalAvatar) {
            return UserAvatarEntity.LocalAvatar(from.type, from.avatarName, from.avatarColor)
        }
        throw GeneralErrorEntity("UnSupported Avatar type")
    }
}