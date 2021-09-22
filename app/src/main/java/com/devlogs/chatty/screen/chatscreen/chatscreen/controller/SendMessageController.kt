package com.devlogs.chatty.screen.chatscreen.chatscreen.controller

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.devlogs.chatty.androidservice.common.IpcServiceConnector
import com.devlogs.chatty.androidservice.sendmessage.SendMessageService
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.textchat.SendTextMessageUseCaseSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SendMessageController @Inject constructor(private val stateManager: PresentationStateManager,
                                                private val sendTextMessageUseCaseSync: SendTextMessageUseCaseSync,
                                                private val ipcServiceConnector: IpcServiceConnector) {
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


    fun send (message: String, channelId: String ) {
        coroutine.launch {
            normalLog("sending message ($message) to channel: $channelId")

            if (ipcServiceConnector.waitingForState(IpcServiceConnector.STATE_BOUND_CONNECTED,CONNECTION_TIMEOUT)) {
                normalLog("Connected to service")


            } else {
                /* Error handling here
                * If you believe that it only goes here when the service has an error occur
                * means this error happen for all others connection => just save into db and service will
                * automatically restore its data in onCreate
                * But if this error happen only to current connection then you should handle the restore
                * */
            }
        }
    }
}