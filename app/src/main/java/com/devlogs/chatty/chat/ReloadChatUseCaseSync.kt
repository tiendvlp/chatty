package com.devlogs.chatty.chat

import com.devlogs.chatty.chat.ReloadChatUseCaseSync.Result.NetworkError
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.process.ConfigurationLocalDbApi
import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ReloadChatUseCaseSync @Inject constructor (private val messageMainServerApi: MessageMainServerApi,
                                                 private val messageLocalDbApi: MessageLocalDbApi,
                                                 private val configurationLocalDbApi: ConfigurationLocalDbApi) {
    sealed class Result {
        data class GeneralError (val message: String) : Result()
        object NetworkError : Result()
        data class Success (val data: List<MessageEntity>): Result()
    }

    suspend fun execute (channelId: String) : Result = withContext(BackgroundDispatcher) {
        try {
            val lastUpdate = configurationLocalDbApi.getMessageLastUpdateTime()
            normalLog("Last update time ${Date(lastUpdate).toLocaleString()}")
            val result = ArrayList<MessageEntity>()
            val messageDto = messageMainServerApi.getChannelMessagesOverPeriodOfTime(channelId, lastUpdate, System.currentTimeMillis())
            result.addAll(messageDto.map {
                MessageEntity(it.id, it.channelId, it.type, it.content, it.senderEmail, it.createdDate)
            })
            saveMessageToDb(channelId, result)
            this@ReloadChatUseCaseSync.normalLog("Reload channel success: " + result.size)
            Result.Success(result)
        } catch (err: AuthenticationErrorEntity) {
            Result.GeneralError("UnAuthorized")
        } catch (err: CommonErrorEntity.GeneralErrorEntity) {
            Result.GeneralError(err.message!!)
        } catch (err:CommonErrorEntity.NetworkErrorEntity) {
            NetworkError
        }
    }

    private suspend fun saveMessageToDb (channelId: String, messages: List<MessageEntity>) = withContext(BackgroundDispatcher) {
         launch(NonCancellable) {
            val messageRos = ArrayList<MessageRealmObject> ()
            messages.forEach {
                messageRos.add(MessageRealmObject(it.id, it.channelId,it.type, it.content, it.senderEmail, it.createdDate))
            }
            messageLocalDbApi.addNewMessages(messageRos)
            val lastUpdate = messageLocalDbApi.getLatestUpdateTime(channelId)
            this@ReloadChatUseCaseSync.normalLog("Last message update saved: " + lastUpdate)
            configurationLocalDbApi.setMessageLastUpdateTime(lastUpdate)
        }
    }
}