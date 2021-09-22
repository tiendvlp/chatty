package com.devlogs.chatty.textchat

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.errorLog
import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.MessageMainServerModel
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity
import com.devlogs.chatty.textchat.SendTextChatFromCacheUseCaseSync.Result.NotFoundInDb
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendTextChatFromCacheUseCaseSync @Inject constructor(val localDbApi: MessageLocalDbApi, val messageMainServerApi: MessageMainServerApi){

    sealed class Result {
        data class Success (val message: MessageEntity) : Result()
        class NetworkError(val messageEntity: MessageEntity)  : Result()
        data class UnAuthorized(val messageEntity: MessageEntity) : Result()
        class GeneralError(val messageEntity: MessageEntity) : Result()
        class NotFoundInDb : Result()
    }

    suspend fun execute (dbId: String) : Result = withContext(BackgroundDispatcher) {
            val dbResult = localDbApi.getMessage(dbId)

            if (dbResult == null) NotFoundInDb()
            val sentMessage = messageMainServerApi.sendTextMessage(dbResult!!.content!!, dbResult!!.channelId!!)
        try {
            replaceMessageInDb(sentMessage)
            Result.Success(MessageEntity(sentMessage.id, sentMessage.channelId, sentMessage.type, sentMessage.content, sentMessage.senderEmail, sentMessage.createdDate, MessageEntity.Status.DONE))
        } catch (e: CommonErrorEntity.NetworkErrorEntity) {
            errorLog("Authentication error happen: " + e.message)
            Result.NetworkError(MessageEntity(
                dbResult.id!!,
                dbResult.channelId!!,
                dbResult.type!!,
                dbResult.content!!,
                dbResult.senderEmail!!,
                dbResult.createdDate!!,
                MessageEntity.Status.FAILED
            ))
        } catch (e: CommonErrorEntity.GeneralErrorEntity) {
            errorLog("GeneralErrorEntity error happen: " + e.message)
            Result.GeneralError(MessageEntity(
                dbResult.id!!,
                dbResult.channelId!!,
                dbResult.type!!,
                dbResult.content!!,
                dbResult.senderEmail!!,
                dbResult.createdDate!!,
                MessageEntity.Status.FAILED
            ))
        } catch (e: AuthenticationErrorEntity.InvalidRefreshTokenErrorEntity) {
            errorLog("InvalidRefreshTokenErrorEntity error happen: " + e.message)
            Result.UnAuthorized(MessageEntity(
                dbResult.id!!,
                dbResult.channelId!!,
                dbResult.type!!,
                dbResult.content!!,
                dbResult.senderEmail!!,
                dbResult.createdDate!!,
                MessageEntity.Status.FAILED
            ))
        }
    }

    private suspend fun replaceMessageInDb (messageFromServer: MessageMainServerModel) = withContext(BackgroundDispatcher) {
        launch {
            localDbApi.delete(messageFromServer.id)
            localDbApi.addOrUpdate(
                MessageRealmObject(
                    id = messageFromServer.id,
                    channelId = messageFromServer.channelId, type = messageFromServer.type,
                    content = messageFromServer.content,
                    senderEmail = messageFromServer.senderEmail,
                    createdDate = messageFromServer.createdDate,
                    "DONE"
                )
            )
        }
    }
}