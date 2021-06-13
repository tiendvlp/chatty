package com.devlogs.chatty.channel

import javax.inject.Inject

class LoadMoreChannelPolicyImp: LoadMoreChannelPolicy {

    @Inject
    constructor() {

    }

    override fun getMaxNumberOfChannel(): Int {
        return 10
    }
}