package com.devlogs.chatty.screen.common

interface BackPressDispatcher {
    fun register (backPressListener: BackPressListener)
    fun unregister (backPressListener: BackPressListener)
}