package com.devlogs.chatty.textchat

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.errorLog
import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.MessageRealmObject
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.MessageMainServerModel
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity.InvalidRefreshTokenErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.textchat.SendTextMessageUseCaseSync.Result.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject



class SendTextMessageUseCaseSync {
         data class Model (
            val channelId: String,val message: String,val spawnedId: String, val createdDate: Long
        )

        interface Listener {
            fun onSending (message: Model)
            fun onSendFailed (result: FailedResult)
            fun onSendSuccess (result: DoneResult)
        }

        sealed class Result (message: Model) {
            sealed class FailedResult (message: Model) : Result (message) {
                data class NetworkError (val message: Model) : FailedResult(message)
                data class GeneralError (val message: Model) : FailedResult(message)
                data class InvalidRefreshToken (val message: Model) : FailedResult(message)
            }

            sealed class DoneResult (message: Model, realId: String) : Result (message) {
                data class Success  (val message: Model, val realId: String) : DoneResult (message, realId)
            }
        }

        private val messageMainServerApi : MessageMainServerApi
        private val messageLocalDbApi: MessageLocalDbApi
        private var spawnedLocalId = UUID.randomUUID().toString()
        var listener: Listener? = null

        @Inject
        constructor(messageMainServerApi : MessageMainServerApi, messageLocalDbApi: MessageLocalDbApi) {
            this.messageMainServerApi = messageMainServerApi
            this.messageLocalDbApi = messageLocalDbApi
        }

        fun getMessageId () : String {
            return spawnedLocalId
        }

        suspend fun execute (senderEmail: String, message: String, channelId: String) = withContext(BackgroundDispatcher) {
            spawnedLocalId = UUID.randomUUID().toString()
            val createdDate = System.currentTimeMillis()
            val messageModel = Model(channelId, message, spawnedLocalId, createdDate)
            try {

                listener?.onSending(messageModel)
                messageLocalDbApi.addOrUpdate(MessageRealmObject(
                    id = spawnedLocalId,
                    channelId = channelId, type = "TEXT",
                    content = message,
                    senderEmail = senderEmail,
                    createdDate = createdDate,
                    "FAILED"
                ))
                val result = messageMainServerApi.sendTextMessage(message, channelId)
                replaceMessageInDb(result)
                listener?.onSendSuccess(DoneResult.Success(messageModel, result.id))
            } catch (e: NetworkErrorEntity) {
                errorLog("Authentication error happen: " + e.message)
                listener?.onSendFailed(FailedResult.NetworkError(messageModel))
            } catch (e: GeneralErrorEntity) {
                errorLog("GeneralErrorEntity error happen: " + e.message)
                listener?.onSendFailed(FailedResult.GeneralError(messageModel))
            } catch (e: InvalidRefreshTokenErrorEntity) {
                errorLog("InvalidRefreshTokenErrorEntity error happen: " + e.message)
                listener?.onSendFailed(FailedResult.InvalidRefreshToken(messageModel))
            }
    }

    private suspend fun replaceMessageInDb (messageFromServer:MessageMainServerModel) = withContext(BackgroundDispatcher){
        launch {
            messageLocalDbApi.delete(messageFromServer.id)
            messageLocalDbApi.addOrUpdate(MessageRealmObject(
                id = messageFromServer.id,
                channelId = messageFromServer.channelId, type = messageFromServer.type,
                content = messageFromServer.content,
                senderEmail = messageFromServer.senderEmail,
                createdDate = messageFromServer.createdDate,
                "DONE"
            ))
        }
    }
}