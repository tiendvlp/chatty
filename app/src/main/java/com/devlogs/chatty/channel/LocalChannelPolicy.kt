package com.devlogs.chatty.channel

interface LocalChannelPolicy {
    companion object {
        val ALLOW_ALL = -1
    }
    fun numberOfChannelAllowed () : Int
}