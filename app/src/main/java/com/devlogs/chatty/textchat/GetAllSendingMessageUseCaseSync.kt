package com.devlogs.chatty.textchat

import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.domain.entity.message.MessageEntity
import javax.inject.Inject

class GetAllSendingMessageUseCaseSync @Inject constructor(private val messageLocalDbApi: MessageLocalDbApi) {
    sealed class Result {
        data class Success (val sendMessages: List<MessageEntity>) : Result()
        class GeneralError () : Result()
        class IOError () : Result()
    }


    suspend fun executes () : Result {
        val queryResult = messageLocalDbApi.getMessageByState(MessageEntity.Status.SENDING)










        val result = queryResult.map ({messageRealmObject -> MessageEntity(messageRealmObject.id!!, messageRealmObject.channelId!!, messageRealmObject.type!!, messageRealmObject.content!!,messageRealmObject.senderEmail!!, messageRealmObject.createdDate!!, MessageEntity.Status.SENDING) })

        return Result.Success(result)

    }

}