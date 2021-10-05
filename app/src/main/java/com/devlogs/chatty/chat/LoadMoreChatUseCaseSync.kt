package com.devlogs.chatty.chat

import com.devlogs.chatty.chat.LoadMoreChatUseCaseSync.Result.*
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.process.ConfigurationLocalDbApi
import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.to
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.domain.entity.message.MessageEntity.*;
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity
import kotlinx.coroutines.withContext
import java.sql.Date
import javax.inject.Inject

class LoadMoreChatUseCaseSync@Inject constructor (private val messageMainServerApi: MessageMainServerApi,
                                                  private val messageLocalDbApi: MessageLocalDbApi,
                                                  private val configurationLocalDbApi: ConfigurationLocalDbApi,
                                                  private val loadMoreChatPolicy: LoadMoreChatPolicy) {
    sealed class Result {
        data class GeneralError (val message: String) : Result()
        object NetworkError : Result()
        data class Success (val data: List<MessageEntity>): Result()
    }

    suspend fun execute (channelId: String, since: Long) : Result = withContext(BackgroundDispatcher) {
        try {
            val neededCount = loadMoreChatPolicy.getMaxNumberOfMessage()
            this@LoadMoreChatUseCaseSync.normalLog("NeddedCount: ${neededCount} at: ${Date(since).toLocaleString()}")
            val result = ArrayList<MessageEntity>()
            result.addAll(messageLocalDbApi.getPreviousMessage(channelId, since, neededCount).map {
                this@LoadMoreChatUseCaseSync.normalLog("Getting from local: ${it.content}")
                MessageEntity(it.id!!, it.channelId!!, it.type!!, it.content!!, it.senderEmail!!, it.createdDate!!, Status.DONE)
            })

            val remainingMessage = neededCount - result.size
            this@LoadMoreChatUseCaseSync.normalLog("Remaining: ${remainingMessage}")
            if (remainingMessage > 0) {
                var since = since
                if (result.isNotEmpty()) {
                    since = result.first().createdDate
                }

                this@LoadMoreChatUseCaseSync.normalLog("Since in Server: ${Date(since).toLocaleString()} ($since)")

                val resultFromServer = loadMoreMessageFromServer(channelId, since, remainingMessage)
                result.addAll(resultFromServer)
            }
            this@LoadMoreChatUseCaseSync.normalLog("LastResult: ${result.size} ")
            Success(result)
        } catch (err: AuthenticationErrorEntity) {
            GeneralError("UnAuthorized")
        } catch (err: CommonErrorEntity.GeneralErrorEntity) {
            GeneralError(err.message!!)
        } catch (err: CommonErrorEntity.NetworkErrorEntity) {
            NetworkError
        }
    }

    private suspend fun loadMoreMessageFromServer (channelId: String, since: Long, count: Int) : List<MessageEntity> {
        val messageMainServerModels = messageMainServerApi.getPreviousChannelMessages(channelId, since, count)
        val results =  messageMainServerModels.map {
            MessageEntity(it._id, it.channelId, it.type, it.content, it.senderEmail, it.createdDate, Status.DONE)
        }
        if (results.isNotEmpty()) {
            saveToLocal (results)
        }
        return results
    }

    private suspend fun saveToLocal (messages: List<MessageEntity>) {
        messageLocalDbApi.addNewMessages(messages.map { it.to() })
    }

}