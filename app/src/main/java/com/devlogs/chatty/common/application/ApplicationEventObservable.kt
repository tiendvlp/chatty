package com.devlogs.chatty.common.application

import androidx.annotation.MainThread
import com.devlogs.chatty.common.base.BaseObservable

class ApplicationEventObservable : BaseObservable<ApplicationListener>() {

    fun getListeners (): Set<ApplicationListener> {
        return super.getListener()
    }

    @MainThread
    internal fun invokeConnectedEvent () {
        getListener().forEach {listener ->
            if (listener is ServerConnectionEvent) {
                listener.onServerConnected()
            }
        }
    }

    @MainThread
    internal fun invokeDisConnectedEvent () {
        getListener().forEach {listener ->
            if (listener is ServerConnectionEvent) {
                listener.onServerDisconnected()
            }
        }
    }
}