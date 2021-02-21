package com.devlogs.chatty.textchat

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity.InvalidRefreshTokenErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.textchat.SendTextMessageUseCaseSync.Result.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendTextMessageUseCaseSync {

    sealed class Result {
        object Success : Result()
        object NetworkError : Result()
        object GeneralError : Result()
        object InvalidRefreshToken : Result ()
    }

    private val mMessageMainServerApi : MessageMainServerApi

    @Inject
    constructor(messageMainServerApi : MessageMainServerApi) {
        mMessageMainServerApi = messageMainServerApi
    }

    suspend fun execute (message: String, channelId: String) : Result = withContext(BackgroundDispatcher) {
        try {
            mMessageMainServerApi.sendTextMessage(message, channelId)
            Success
        } catch (e: NetworkErrorEntity) {
            NetworkError
        } catch (e: GeneralErrorEntity) {
            GeneralError
        } catch (e: InvalidRefreshTokenErrorEntity) {
            InvalidRefreshToken
        }
    }
}