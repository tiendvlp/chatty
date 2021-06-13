package com.devlogs.chatty.common.application

import android.app.Application
import android.os.Handler
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.domain.datasource.local.TokenOfflineApi
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.realtime.MessageRealtime
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), MessageRealtime.Listener {
    @Inject
    lateinit var socketInstance: Socket
    @Inject
    lateinit var messageRealtime: MessageRealtime
    @Inject
    lateinit var mTokenApi : TokenOfflineApi
    @Inject
    lateinit var applicationEventObservable: ApplicationEventObservable
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        socketInstance.connect()
        socketInstance.on(Socket.EVENT_CONNECT) {
            coroutineScope.launch {
                applicationEventObservable.invokeConnectedEvent()
            }
        }
        socketInstance.on(Socket.EVENT_DISCONNECT) {
            coroutineScope.launch {
                applicationEventObservable.invokeDisConnectedEvent()
            }
        }
        messageRealtime.register(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        socketInstance.disconnect()
    }

    override fun onNewMessage(messageEntity: MessageEntity) {
        normalLog("NewMessageComming: ${messageEntity.content}")
    }
}