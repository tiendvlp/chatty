package com.devlogs.chatty.channel

class LoadMoreChannelPolicyImp: LoadMoreChannelPolicy {
    override fun getMaxNumberOfChannel(): Int {
        return 10
    }
}