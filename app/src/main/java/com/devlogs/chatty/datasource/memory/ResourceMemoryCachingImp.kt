package com.devlogs.chatty.datasource.memory

import com.devlogs.chatty.domain.datasource.memory.ResourceMemoryCaching

class ResourceMemoryCachingImp : ResourceMemoryCaching  {

    override fun getAvatar(userEmail: String): ByteArray {
        return null!!
    }
}