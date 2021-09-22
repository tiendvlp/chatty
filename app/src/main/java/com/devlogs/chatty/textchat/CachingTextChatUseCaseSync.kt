package com.devlogs.chatty.textchat

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.errorLog
import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import com.devlogs.chatty.domain.entity.message.MessageEntity
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

class CachingTextChatUseCaseSync @Inject constructor(private val localDbAPi: MessageLocalDbApi){

    sealed class Result {
        data class Success (val messageEntity: MessageEntity) : Result()
        class Failed () : Result()
    }

    suspend fun execute (channelId: String, message: String, senderEmail: String) : Result = withContext(BackgroundDispatcher) {
        val createdDate = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()
        try {
            localDbAPi.addOrUpdate(
                MessageRealmObject(
                    id = id,
                    channelId = channelId, type = "TEXT",
                    content = message,
                    senderEmail = senderEmail,
                    createdDate = createdDate,
                    "FAILED"
                )
            )
            Result.Success(
                MessageEntity(
                    id,
                    channelId,
                    "TEXT",
                    message,
                    senderEmail,
                    createdDate,
                    MessageEntity.Status.SENDING
                )
            )
        } catch (ex: IllegalArgumentException) {
            errorLog("Can not save text chat to db due to: ${ex.message}")
            Result.Failed()
        }
    }
}