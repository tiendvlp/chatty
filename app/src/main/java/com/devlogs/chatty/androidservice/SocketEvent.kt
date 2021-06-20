package com.devlogs.chatty.androidservice

import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.message.MessageEntity

object SocketEventType {
    const val NEW_MESSAGE = "newMessage"
    const val NEW_CHANNEL = "newChannel"
}

interface SocketListener {

}

interface SocketConnectionListener : SocketListener {
    fun onSocketConnected ()
    fun onSocketDisconnected ()
}

interface SocketChannelListener : SocketListener {
    fun onNewChannel (newChannel: ChannelEntity)
    fun onChannelUpdate (updatedChannel: ChannelEntity)
}

interface SocketMessageListener : SocketListener {
    fun onNewMessage (newMessage: MessageEntity)
}

