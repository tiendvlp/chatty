package com.devlogs.chatty.realtime

import com.devlogs.chatty.androidservice.SocketEventObservable
import com.devlogs.chatty.common.base.BaseObservable
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.realtime.MessageRealtime.Listener
import io.socket.client.Socket
import org.json.JSONObject
import javax.inject.Inject

class MessageRealtime : BaseObservable<Listener> {
    interface Listener {
        fun onNewMessage(messageEntity: MessageEntity)
    }

    private val socketInstance: Socket

    @Inject
    constructor(socketInstance: Socket) {
        this.socketInstance = socketInstance
        startListen()
    }

    private fun startListen () {
        socketInstance.on("newMessage") {args ->
            val jsonData = JSONObject(args[0].toString())
            val newMessageEntity = MessageEntity(
                jsonData.getString("_id"),
                jsonData.getString("channelId"),
                jsonData.getString("type"),
                jsonData.getString("content"),
                jsonData.getString("senderEmail"),
                jsonData.getLong("createdDate")
            )
            getListener().forEach {
                it.onNewMessage(newMessageEntity)
            }
        }
    }
}