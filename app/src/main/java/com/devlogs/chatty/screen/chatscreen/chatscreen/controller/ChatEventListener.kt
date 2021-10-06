package com.devlogs.chatty.screen.chatscreen.chatscreen.controller

import com.devlogs.chatty.androidservice.socket.SocketEventObservable
import com.devlogs.chatty.androidservice.socket.SocketMessageListener
import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.to
import com.devlogs.chatty.screen.chatscreen.chatscreen.state.ChatScreenAction
import com.devlogs.chatty.screen.chatscreen.chatscreen.state.ChatScreenState
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import javax.inject.Inject

class ChatEventListener @Inject constructor(private var socketEventObservable: SocketEventObservable, private val stateManager: PresentationStateManager) :
    SocketMessageListener {
    private var isStarted = false
    fun onStart() {
        if (isStarted) return
        socketEventObservable.register(this)
        isStarted = true
    }

    fun onStop () {
        if(!isStarted) return
        socketEventObservable.unRegister(this)
        isStarted = false
    }

    override fun onNewMessage(newMessage: MessageEntity) {
        normalLog("NewMessage: " + newMessage.content)
        if (newMessage.senderEmail.equals(SharedMemory.email)) {
            return;
        }
        if (stateManager.currentState is ChatScreenState.DisplayState) {
            stateManager.consumeAction(ChatScreenAction.NewChatAction(newMessage.to()))
        }
    }
}