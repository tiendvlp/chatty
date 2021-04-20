package com.devlogs.chatty.common

import android.app.Application
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.realtime.MessageRealtime
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.socket.client.Socket
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), MessageRealtime.Listener {
    @Inject
    lateinit var socketInstance: Socket
    @Inject
    lateinit var messageRealtime: MessageRealtime
    private val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pbmd0aW5nMTdAbWludGluLmNvbSIsImlkIjoiNWZiNmFkZmI1YzhhNjgwN2MwZDM4NmJkIiwiaWF0IjoxNjE4OTI1OTcyLCJleHAiOjE2MTkwMTIzNzJ9.LofuBNM_eFfQ9SrWPa25bh1YvdR62naAFChwu3kqhG8"
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        socketInstance.emit("joinRoom", testToken)
        messageRealtime.register(this)
        socketInstance.connect()
    }

    override fun onTerminate() {
//        Realm().apply(this)
        super.onTerminate()
        socketInstance.disconnect()
    }

    override fun onNewMessage(messageEntity: MessageEntity) {
        normalLog("NewMessageComming: ${messageEntity.content}")
    }
}