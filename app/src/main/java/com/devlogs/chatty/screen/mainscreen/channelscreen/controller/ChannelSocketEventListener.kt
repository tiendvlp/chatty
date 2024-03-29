package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import com.devlogs.chatty.androidservice.socket.SocketChannelListener
import com.devlogs.chatty.androidservice.socket.SocketEventObservable
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.to
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChannelSocketEventListener @Inject constructor(
    private val socketEventObservable: SocketEventObservable,
    private val presentationStateManager: PresentationStateManager) : SocketChannelListener {
    private val coroutine: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var isStarted = false;
    fun onStart () {
        if (isStarted) return
        socketEventObservable.register(this)
        isStarted = true
    }

    fun onStop () {
        if (!isStarted) return
        socketEventObservable.unRegister(this)
        isStarted = false
    }

    override fun onNewChannel(newChannel: ChannelEntity) {
        normalLog("onNewchannel: ${newChannel.latestUpdate}")
        coroutine.launch {
            val channelPM = withContext(BackgroundDispatcher) { newChannel.to() }
            presentationStateManager.consumeAction(
                ChannelScreenPresentationAction.NewChannelAction(channelPM)
            )
        }
    }

    override fun onChannelUpdate(updatedChannel: ChannelEntity) {
        normalLog("onChannel Updated: ${updatedChannel.latestUpdate}")
        coroutine.launch {
            val channelPM = withContext(BackgroundDispatcher) { updatedChannel.to() }
            presentationStateManager.consumeAction(
                ChannelScreenPresentationAction.ChannelUpdatedAction(channelPM)
            )
        }
    }

}