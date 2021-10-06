package com.devlogs.chatty.textchat

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.errorLog
import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.MessageMainServerModel
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity.InvalidRefreshTokenErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject



class SendTextMessageUseCaseSync {
        enum class FAILED_STATUS {
            GENERAL_ERROR, NETWORK_ERROR, AUTH_ERROR
        }

        interface Listener {
            fun onSending () {}
            fun onCachedToLocalDb () {}
            fun onSendMessageSuccess (message: MessageEntity) {}
            fun onSendToServerFailed (message: MessageEntity) {}
        }

        private val messageMainServerApi : MessageMainServerApi
        private val messageLocalDbApi: MessageLocalDbApi

        @Inject
        constructor(messageMainServerApi : MessageMainServerApi, messageLocalDbApi: MessageLocalDbApi) {
            this.messageMainServerApi = messageMainServerApi
            this.messageLocalDbApi = messageLocalDbApi
        }


        suspend fun execute (id: String? = null, senderEmail: String, message: String, channelId: String, callBack: Listener? = null) = withContext(BackgroundDispatcher) {
            var messageId = id
            if (messageId == null) {
                messageId = UUID.randomUUID().toString().substring(0,8)
            }
            val createdDate = System.currentTimeMillis()
            try {
                callBack?.onSending()
                messageLocalDbApi.addOrUpdate(
                    MessageRealmObject(
                        id = messageId,
                        channelId = channelId, type = "TEXT",
                        content = message,
                        senderEmail = senderEmail,
                        createdDate = createdDate,
                        "FAILED"
                    )
                )

                val result = messageMainServerApi.sendTextMessage(message, channelId)
                val sentMessage = MessageEntity(
                    result._id,
                    channelId,
                    result.type,
                    result.content,
                    result.senderEmail,
                    result.createdDate,
                    MessageEntity.Status.DONE
                )
                replaceMessageInDb(messageId, result);
                callBack?.onSendMessageSuccess(sentMessage);
            } catch (e: NetworkErrorEntity) {
                errorLog("Authentication error happen: " + e.message)
                callBack?.onSendToServerFailed(MessageEntity(messageId, channelId, "TEXT", message, senderEmail, createdDate, MessageEntity.Status.FAILED))
            } catch (e: GeneralErrorEntity) {
                errorLog("GeneralErrorEntity error happen: " + e.message)
                callBack?.onSendToServerFailed(MessageEntity(messageId, channelId, "TEXT", message, senderEmail, createdDate, MessageEntity.Status.FAILED))
            } catch (e: InvalidRefreshTokenErrorEntity) {
                errorLog("InvalidRefreshTokenErrorEntity error happen: " + e.message)
                callBack?.onSendToServerFailed(MessageEntity(messageId, channelId, "TEXT", message, senderEmail, createdDate, MessageEntity.Status.FAILED))
            }
    }

    private suspend fun replaceMessageInDb (id: String, messageFromServer:MessageMainServerModel) = withContext(BackgroundDispatcher){

        launch {
            messageLocalDbApi.delete(id)
            messageLocalDbApi.addOrUpdate(MessageRealmObject(
                id = messageFromServer._id,
                channelId = messageFromServer.channelId, type = messageFromServer.type,
                content = messageFromServer.content,
                senderEmail = messageFromServer.senderEmail,
                createdDate = messageFromServer.createdDate,
                "DONE"
            ))
        }
    }
}