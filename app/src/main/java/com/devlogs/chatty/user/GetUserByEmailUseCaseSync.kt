package com.devlogs.chatty.user

import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.UserAvatarMainServerModel
import com.devlogs.chatty.domain.entity.user.UserAvatarEntity
import com.devlogs.chatty.domain.entity.user.UserEntity
import com.devlogs.chatty.domain.error.ErrorEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserByEmailUseCaseSync {
    sealed class Result {
        data class Success(val userEntity: UserEntity) : Result()
        object UserNotFoundError : Result()
        object GeneralError : Result ()
    }

    private val mUserMainServerApi : UserMainServerApi

    @Inject
    constructor(userMainServerApi: UserMainServerApi) {
        mUserMainServerApi = userMainServerApi
    }

    suspend fun execute (userEmail: String) : Result = withContext(Dispatchers.IO) {
        try {
            val result = mUserMainServerApi.getUser(userEmail)
            return@withContext Result.Success(UserEntity(result.id, result.name, result.email, getUserAvatarEntity(result.avatar)))
        } catch(e: CancellationException) {
            withContext(NonCancellable) {
            }
        } catch (e: ErrorEntity.UnSupportedDataTypeError) {
            return@withContext Result.GeneralError
        } catch (e: ErrorEntity.MissingDataError) {
            return@withContext Result.GeneralError
        } catch (e: ErrorEntity.UnknownError) {
            return@withContext Result.GeneralError
        } catch (e: ErrorEntity.NotFoundError) {
            return@withContext Result.UserNotFoundError
        }
        null!!
    }

    private fun getUserAvatarEntity (from: UserAvatarMainServerModel) : UserAvatarEntity {
        if (from is UserAvatarMainServerModel.LocalAvatar) {
            return UserAvatarEntity.LocalAvatar(from.type, from.avatarName, from.avatarColor)
        }

        throw ErrorEntity.UnSupportedDataTypeError
    }
}