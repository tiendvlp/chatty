package com.devlogs.chatty.androidservice

import com.devlogs.chatty.common.base.Observable

class SocketEventObservable : Observable<SocketListener> {

    private val listeners: HashSet<SocketListener> = HashSet()

    override fun register(listener: SocketListener) {
        listeners.add(listener)
    }

    override fun unRegister(listener: SocketListener) {
        listeners.remove(listener)
    }

    internal fun getListeners () : HashSet<SocketListener> {
        return listeners
    }

}