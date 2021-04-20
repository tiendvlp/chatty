package com.devlogs.chatty.channel

interface LoadMoreChannelPolicy {
    fun getMaxNumberOfChannel () : Int
}