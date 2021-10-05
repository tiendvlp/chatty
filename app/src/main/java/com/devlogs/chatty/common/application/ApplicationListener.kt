package com.devlogs.chatty.common.application

import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.message.MessageEntity

interface ApplicationListener {

}

interface ServerConnectionEvent : ApplicationListener {
    fun onServerDisconnected ()
    fun onServerConnected ()
}

interface ChannelListener : ApplicationListener {
    fun onNewChannel(newChannel: ChannelEntity)
    fun onChannelUpdate (updatedChannel: ChannelEntity)
}

interface MessageListener: ApplicationListener {
    fun onNewMessage (newMessage: MessageEntity) {}
    fun onMessageStatusChanged (changedMessage: MessageEntity) {}
}


