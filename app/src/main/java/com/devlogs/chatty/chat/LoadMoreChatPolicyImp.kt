package com.devlogs.chatty.chat

import javax.inject.Inject

class LoadMoreChatPolicyImp : LoadMoreChatPolicy {

    @Inject
    constructor()

    override fun getMaxNumberOfMessage(): Int {
        return 20
    }
}