package com.devlogs.chatty.user

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.mapper.Mapper
import com.devlogs.chatty.datasource.local.process.UserLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.toUserEntity
import com.devlogs.chatty.datasource.local.relam_object.toUserRO
import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.toUserEntity
import com.devlogs.chatty.domain.entity.user.UserEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.user.GetUserByEmailUseCaseSync.Result.*
import kotlinx.coroutines.launch
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
    private val userLocalDbApi : UserLocalDbApi

    @Inject
    constructor(userMainServerApi: UserMainServerApi, userLocalDbApi: UserLocalDbApi) {
        mUserMainServerApi = userMainServerApi
        this.userLocalDbApi = userLocalDbApi
    }

    suspend fun execute (userEmail: String) : Result = withContext(BackgroundDispatcher) {
        try {
            var result : UserEntity? = userLocalDbApi.getUser(userEmail)?.let {
                Mapper().toUserEntity(
                    it
                )
            }
            if (result == null) {
                result = Mapper().toUserEntity(mUserMainServerApi.getUser(userEmail))
                launch {
                    userLocalDbApi.addUser(Mapper().toUserRO(result))
                }
            }

            Success(result)
        } catch (e: GeneralErrorEntity) {
            GeneralError
        } catch (e: NotFoundErrorEntity) {
            UserNotFoundError
        } catch (e: NetworkErrorEntity) {
            NetworkError
        }
    }

}