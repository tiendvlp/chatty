package com.devlogs.chatty.common.base

interface Observable<LISTENER> {
    fun register (listener: LISTENER)
    fun unRegister (listener: LISTENER)
}