package com.devlogs.chatty.androidservice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.devlogs.chatty.channel.UpdateChannelStatusUseCaseSync
import com.devlogs.chatty.common.application.ApplicationEventObservable
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.to
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelMemberEntity
import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity
import com.devlogs.chatty.domain.entity.message.MessageEntity
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

fun Context.bindSocketEventService (connection: ServiceConnection, flag: Int = Context.BIND_AUTO_CREATE) {
    val intent = Intent(this, SocketEvenService::class.java)
    bindService(intent, connection, flag)
}

@AndroidEntryPoint
class SocketEvenService : Service() {
    inner class LocalBinder : Binder() {
        val service = this@SocketEvenService
    }

    @Inject
    protected lateinit var socketInstance: Socket
    @Inject
    protected lateinit var applicationEventObservable: ApplicationEventObservable
    @Inject
    protected lateinit var socketEventObservable: SocketEventObservable
    @Inject
    protected lateinit var updateChannelStatusUseCase: UpdateChannelStatusUseCaseSync
    @Inject
    protected lateinit var channelLocalDbApi: ChannelLocalDbApi

    private lateinit var binder: LocalBinder
    private val coroutine = CoroutineScope(Dispatchers.Main.immediate)
    override fun onCreate() {
        super.onCreate()
        binder = LocalBinder()
        socketInstance.connect()
    }

    override fun onBind(intent: Intent?): IBinder {
        registerSocketConnectionEvent()
        registerSocketMessageEvent()
        registerSocketChannelEvent()
        return binder
    }

    override fun onDestroy() {
        socketInstance.disconnect()
        super.onDestroy()
    }

    private fun registerSocketConnectionEvent () {
        normalLog("Connected")
        socketInstance.on(Socket.EVENT_CONNECT) {
            coroutine.launch {
                applicationEventObservable.invokeConnectedEvent()
            }
        }
        socketInstance.on(Socket.EVENT_DISCONNECT) {
            normalLog("Disconnected")
            coroutine.launch {
                applicationEventObservable.invokeDisConnectedEvent()
            }
        }
    }

    private fun registerSocketMessageEvent () {
            socketInstance.on(SocketEventType.NEW_MESSAGE) { payload ->
                val jsonData = JSONObject(payload[0].toString())
                normalLog("NewMessageComing: ${jsonData.getString("content")} || ${jsonData.getString("createdDate")}")
                val newMessageEntity = MessageEntity(
                    jsonData.getString("_id"),
                    jsonData.getString("channelId"),
                    jsonData.getString("type"),
                    jsonData.getString("content"),
                    jsonData.getString("senderEmail"),
                    jsonData.getLong("createdDate")
                )
                updateChannelStatusWhenMessageCome(newMessageEntity)
                socketEventObservable.getListeners().forEach { listener ->
                    if (listener is SocketMessageListener) {
                        listener.onNewMessage(newMessageEntity)
                        listener
                    }
                }
        }
    }

    private  fun updateChannelStatusWhenMessageCome (messageEntity: MessageEntity) {
        coroutine.launch {
           val result =  updateChannelStatusUseCase.execute(messageEntity.channelId, ChannelStatusEntity(
                messageEntity.senderEmail,
                messageEntity.content,
                messageEntity.type), messageEntity.createdDate)

        if (result is UpdateChannelStatusUseCaseSync.Result.Success) {
            socketEventObservable.getListeners().forEach { listener ->
                if (listener is SocketChannelListener) {
                    listener.onChannelUpdate(result.channel)
                }
            }
        }
        }
    }


    private fun registerSocketChannelEvent () {
        socketInstance.on(SocketEventType.NEW_CHANNEL) { payload ->
            val jsonData = JSONObject(payload[0].toString())
            val jsonStatus = jsonData.getJSONObject("status")
            val jsonMems = jsonData.getJSONArray("members")
            val jsonSeen = jsonData.getJSONArray("seen")
            val members = ArrayList<ChannelMemberEntity> ()
            val seenPeoples = ArrayList<String> ()
            var jsonMem: JSONObject
            for (i in 0 until jsonMems.length()) {
                jsonMem = jsonMems.getJSONObject(i)
                members.add(
                    ChannelMemberEntity(
                    id = jsonMem.getString("id"),
                    email =  jsonMem.getString("email")
                ))
            }

            for (i in 0 until  jsonSeen.length()) {
                seenPeoples.add(jsonSeen.getString(i))
            }

            val newChannelEntity = ChannelEntity(
                id = jsonData.getString("_id"),
                title = jsonData.getString("title"),
                admin = jsonData.getString("admin"),
                status = ChannelStatusEntity(
                    senderEmail = jsonStatus.getString("senderEmail"),
                    content = jsonStatus.getString("content"),
                    type = jsonStatus.getString("type")
                ),
                members = members,
                seen = seenPeoples,
                createdDate = jsonData.getLong("createdDate"),
                latestUpdate = jsonData.getLong("latestUpdate")
            )
            normalLog("NewChannelCreated: ${jsonData.getLong("latestUpdate")}")
            coroutine.launch {
                withContext(BackgroundDispatcher) {
                    channelLocalDbApi.addChannel(newChannelEntity.to())
                }
            }

            socketEventObservable.getListeners().forEach { listener ->
                if (listener is SocketChannelListener) {
                    listener.onNewChannel(newChannelEntity)
                }
            }
        }
    }

}