package com.devlogs.chatty.screen.common.mvcview

interface ObservableMvcView<LISTENER> : MvcView {
    fun register (listener: LISTENER)
    fun unRegister (listener: LISTENER)
}