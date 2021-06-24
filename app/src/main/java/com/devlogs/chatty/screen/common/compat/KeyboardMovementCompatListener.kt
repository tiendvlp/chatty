package com.devlogs.chatty.screen.common.compat

interface KeyboardMovementCompatListener {
    fun callback (delta: Int, distance: Int, maxDistance: Int)
}