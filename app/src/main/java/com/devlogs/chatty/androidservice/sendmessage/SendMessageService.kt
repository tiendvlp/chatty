package com.devlogs.chatty.androidservice.sendmessage

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.devlogs.chatty.common.application.ApplicationEventObservable
import com.devlogs.chatty.common.application.ApplicationListener
import com.devlogs.chatty.common.application.MessageListener
import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.textchat.CachingTextChatUseCaseSync
import com.devlogs.chatty.textchat.SendTextChatFromCacheUseCaseSync
import com.devlogs.chatty.textchat.SendTextMessageUseCaseSync
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private lateinit var binder: LocalBinder
    private val coroutine = CoroutineScope(Dispatchers.Main.immediate)
    private val cachingQueue: Queue<MessageQueueModel> = LinkedList()
    private val sendingQueue: Queue<MessageSendingQueueModel> = LinkedList()

    @Inject
    protected lateinit var applicationEventObservable: ApplicationEventObservable

    @Inject
    protected lateinit var cachingTextMessageUseCase: CachingTextChatUseCaseSync

    @Inject
    protected lateinit var sendTextMessageUseCase: SendTextChatFromCacheUseCaseSync

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun sendTextMessage(channelId: String, content: String) {
        cachingQueue.add(MessageQueueModel(channelId, content))
        startCachingTextMessageProcess()
    }

    private var isCachingTextMessageProcessing = false
    private fun startCachingTextMessageProcess() {
        if (isCachingTextMessageProcessing) return

        coroutine.launch(BackgroundDispatcher) {
            isCachingTextMessageProcessing = true
            var textMessage: MessageQueueModel? = null
            while (textMessage != null) {
                textMessage = cachingQueue.peek()
                val result = cachingTextMessageUseCase.execute(
                    textMessage.channelId,
                    textMessage.content,
                    SharedMemory.email!!
                )
                applicationEventObservable.getListeners().forEach {
                    if (it is MessageListener) {
                        if (result is CachingTextChatUseCaseSync.Result.Success) {
                            // current message state: sending
                            it.onNewMessage(result.messageEntity)
                            sendingQueue.add(MessageSendingQueueModel(result.messageEntity.id))
                            startSendTextMessageProcess()
                        }
                    }
                }
            }
        }
        isCachingTextMessageProcessing = false
    }


    private var isSendTextMessageProcessing = false
    private fun startSendTextMessageProcess() {
        if (isSendTextMessageProcessing) return

        coroutine.launch(BackgroundDispatcher) {
            isSendTextMessageProcessing = true
            var textMessage: MessageSendingQueueModel? = null
            while (textMessage != null) {
                textMessage = sendingQueue.peek()
                val result = sendTextMessageUseCase.execute(textMessage.id)
                applicationEventObservable.getListeners().forEach {
                    if (it is MessageListener) {
                        if (result is SendTextChatFromCacheUseCaseSync.Result.Success) {
                            it.onNewMessage(result.message)
                        }
                    }
                }
            }
            isSendTextMessageProcessing = false
        }
    }
}