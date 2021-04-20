package com.devlogs.chatty.channel

import com.devlogs.chatty.channel.LocalChannelPolicy.Companion.ALLOW_ALL

class AllLocalChannelPolicyImp : LocalChannelPolicy {
    override fun numberOfChannelAllowed(): Int = ALLOW_ALL
}
