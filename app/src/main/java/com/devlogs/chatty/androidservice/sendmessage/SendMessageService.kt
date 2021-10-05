package com.devlogs.chatty.androidservice.sendmessage

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.devlogs.chatty.common.application.ApplicationEventObservable
import com.devlogs.chatty.common.application.MessageListener
import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.process.MessageLocalDbApi
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.textchat.GetAllSendingMessageUseCaseSync
import com.devlogs.chatty.textchat.SendTextMessageUseCaseSync
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SendMessageService : Service() {
    inner class LocalBinder : Binder() {
        val service = this@SendMessageService
    }

    companion object {
        fun bind(
            context: Context,
            connection: ServiceConnection,
            flag: Int = Context.BIND_AUTO_CREATE
        ) {
            val intent = Intent(context, SendMessageService::class.java)
            context.bindService(intent, connection, flag)
        }
    }

    private val coroutine = CoroutineScope(Dispatchers.Main.immediate)
    private val sendingQueue: Queue<MessageSendingQueueModel> = LinkedList()

    @Inject
    protected lateinit var applicationEventObservable: ApplicationEventObservable

    @Inject
    protected lateinit var messageLocalDbApi: MessageLocalDbApi

    @Inject
    protected lateinit var sendTextMessageUseCase: SendTextMessageUseCaseSync
    @Inject
    protected lateinit var getAllSendingMessageUseCaseSync: GetAllSendingMessageUseCaseSync

    override fun onBind(intent: Intent?): IBinder? {
        coroutine.launch {
            restoreSendingQueue()
            startSendTextMessageProcess()
        }
        return LocalBinder()
    }

    private suspend fun restoreSendingQueue () {
        (getAllSendingMessageUseCaseSync.executes() as GetAllSendingMessageUseCaseSync.Result.Success).sendMessages.forEach {
            sendingQueue.add(MessageSendingQueueModel(it.id, it.senderEmail, it.content, it.channelId))
        }
    }

    fun sendTextMessage(channelId: String, content: String, identify: String?) {
        sendingQueue.add(MessageSendingQueueModel(identify, SharedMemory.email!!, content, channelId))
        coroutine.launch {
            startSendTextMessageProcess()
        }
    }

    private var isSendTextMessageProcessing = false
    private suspend fun startSendTextMessageProcess() = withContext(BackgroundDispatcher) {
        if (isSendTextMessageProcessing) return@withContext
        isSendTextMessageProcessing = true

        var textMessage: MessageSendingQueueModel? = sendingQueue.poll()
        while (textMessage != null) {
            val listener: SendTextMessageUseCaseSync.Listener = object : SendTextMessageUseCaseSync.Listener {
                override fun onSendMessageSuccess(message: MessageEntity) {
                    applicationEventObservable.getListeners().forEach {
                        if (it is MessageListener) {
                            it.onMessageStatusChanged(message)
                        }
                    }
                }

                override fun onSendToServerFailed(message: MessageEntity) {
                    applicationEventObservable.getListeners().forEach {
                        if (it is MessageListener) {
                            it.onMessageStatusChanged(message)
                        }
                    }
                }

            }
            sendTextMessageUseCase.execute(
                textMessage.id,
                textMessage.senderEmail,
                textMessage.content,
                textMessage.channelId,
                listener
            )
            textMessage = sendingQueue.poll()
        }

        isSendTextMessageProcessing = false
    }
}