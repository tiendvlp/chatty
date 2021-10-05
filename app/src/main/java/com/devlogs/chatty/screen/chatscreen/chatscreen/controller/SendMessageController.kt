package com.devlogs.chatty.screen.chatscreen.chatscreen.controller

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.devlogs.chatty.androidservice.sendmessage.SendMessageService
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.textchat.SendTextMessageUseCaseSync
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class SendMessageController  {
    private val coroutine = CoroutineScope(BackgroundDispatcher)
    private var sendMessageService : SendMessageService? = null
    private val CONNECTION_TIMEOUT : Long = 7000

    val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            sendMessageService = (service as SendMessageService.LocalBinder).service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            sendMessageService = null
        }

    }

    private val stateManager: PresentationStateManager
    private val sendTextMessageUseCaseSync: SendTextMessageUseCaseSync

    @Inject
    constructor(stateManager: PresentationStateManager, sendTextMessageUseCaseSync: SendTextMessageUseCaseSync) {
        this.stateManager = stateManager;
        this.sendTextMessageUseCaseSync = sendTextMessageUseCaseSync;

//        SendMessageService.bind(require,connection)
    }


    fun send (message: String, channelId: String ) {
            normalLog("sending message ($message) to channel: $channelId")
            sendMessageService?.sendTextMessage(channelId, message, "")
    }
}