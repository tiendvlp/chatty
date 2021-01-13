package com.devlogs.chatty.user

import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi.CreateUserError.UserAlreadyExist
import com.devlogs.chatty.domain.datasource.mainserver.model.UserAvatarMainServerModel
import com.devlogs.chatty.domain.entity.user.UserAvatarEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.user.CreateUserUseCaseSync.Result.GeneralError
import com.devlogs.chatty.user.CreateUserUseCaseSync.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateUserUseCaseSync {
    sealed class Result {
        object Success : Result()
        object NetworkError : Result()
        object GeneralError : Result ()
        object UnAuthorize : Result()
        object UserAlreadyExist : Result()
    }

    private val mUserMainServerApi : UserMainServerApi

    constructor(userMainServerApi: UserMainServerApi) {
        this.mUserMainServerApi = userMainServerApi
    }

    suspend fun execute (name: String, userAvatar: UserAvatarEntity) : Result = withContext(Dispatchers.IO) {
        try {
            val avatar = convertAvatarEntityToAvatarServerModel(userAvatar) ?: return@withContext GeneralError
            mUserMainServerApi.createUser(name, avatar)
            Success
        } catch (e: GeneralErrorEntity) {
            GeneralError
        } catch (e: NetworkErrorEntity) {
            Result.NetworkError
        } catch (e: UserAlreadyExist) {
            GeneralError
        } catch (e: TokenExpiredErrorEntity) {
            Result.UnAuthorize
        }
    }

    private suspend fun convertAvatarEntityToAvatarServerModel (avatarEntity: UserAvatarEntity) : UserAvatarMainServerModel? = withContext(Dispatchers.Default) {
        if (avatarEntity is UserAvatarEntity.LocalAvatar) {
            UserAvatarMainServerModel.LocalAvatar("LOCAL", avatarEntity.avatarName, avatarEntity.avatarColor)
        }
        null
    }

}