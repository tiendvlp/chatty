package com.devlogs.chatty.common.application

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.devlogs.chatty.androidservice.SocketConnectionListener
import com.devlogs.chatty.androidservice.SocketEventObservable
import com.devlogs.chatty.androidservice.bindSocketEventService
import com.devlogs.chatty.chat.spawnChat
import com.devlogs.chatty.domain.datasource.local.TokenOfflineApi
import com.devlogs.chatty.realtime.MessageRealtime
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


@HiltAndroidApp
class MyApplication : Application(), SocketConnectionListener, ServiceConnection {
    companion object {
         var  appContext: Context? = null; private set
         var gson: Gson? = null; public get private set
         var inForeground = false; private set
    }
    @Inject
    lateinit var socketInstance: Socket
    @Inject
    lateinit var messageRealtime: MessageRealtime
    @Inject
    lateinit var mTokenApi : TokenOfflineApi
    @Inject
    lateinit var applicationEventObservable: ApplicationEventObservable
    @Inject
    lateinit var socketEventObservable: SocketEventObservable
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        gson = GsonBuilder().create()
        Realm.init(this)
        socketEventObservable.register(this)
        applicationContext.bindSocketEventService(this)
        spawnChat()
    }

    override fun onTerminate() {
        super.onTerminate()
        socketInstance.disconnect()
        appContext = null;
    }

    override fun onSocketConnected() {
        applicationEventObservable.invokeConnectedEvent()
    }

    override fun onSocketDisconnected() {
        applicationEventObservable.invokeDisConnectedEvent()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        socketEventObservable.register(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        socketEventObservable.unRegister(this)
    }
}