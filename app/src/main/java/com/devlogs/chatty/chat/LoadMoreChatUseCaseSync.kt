package com.devlogs.chatty.chat

import com.devlogs.chatty.chat.LoadMoreChatUseCaseSync.Result.*
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.process.ConfigurationLocalDbApi
import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadMoreChatUseCaseSync@Inject constructor (private val messageMainServerApi: MessageMainServerApi,
                                                  private val messageLocalDbApi: MessageLocalDbApi,
                                                  private val configurationLocalDbApi: ConfigurationLocalDbApi,
                                                  private val loadMoreChatPolicy: LoadMoreChatPolicy
) {
    sealed class Result {
        data class GeneralError (val message: String) : Result()
        object NetworkError : Result()
        data class Success (val data: List<MessageEntity>): Result()
    }

    suspend fun execute (channelId: String, since: Long) : Result = withContext(BackgroundDispatcher) {
        try {
            val neededCount = loadMoreChatPolicy.getMaxNumberOfMessage()
            val result = ArrayList<MessageEntity>()
            result.addAll(messageLocalDbApi.getPreviousMessage(channelId, since, neededCount).map {
                MessageEntity(it.id!!, it.channelId!!, it.type!!, it.content!!, it.senderEmail!!, it.createdDate!!)
            })

            val remainingMessage = neededCount - result.size

            if (remainingMessage > 0) {
                val resultFromServer = loadMoreMessageFromServer(channelId, since, remainingMessage)
                result.addAll(resultFromServer)
            }

            Success(result)
        } catch (err: AuthenticationErrorEntity) {
            GeneralError("UnAuthorized")
        } catch (err: CommonErrorEntity.GeneralErrorEntity) {
            GeneralError(err.message!!)
        } catch (err: CommonErrorEntity.NetworkErrorEntity) {
            NetworkError
        }
    }

    suspend fun loadMoreMessageFromServer (channelId: String, since: Long, count: Int) : List<MessageEntity> {
        val messageMainServerModels = messageMainServerApi.getPreviousChannelMessages(channelId, since, count)
        return messageMainServerModels.map {
            MessageEntity(it.id, it.channelId, it.type, it.content, it.senderEmail, it.createdDate)
        }
    }
}