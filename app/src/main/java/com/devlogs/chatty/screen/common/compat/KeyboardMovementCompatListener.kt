package com.devlogs.chatty.screen.common.compat

interface KeyboardMovementCompatListener {
    fun onStart ()
    fun onFinished ()
    fun onProgress (delta: Int, distance: Int, maxDistance: Int)
}