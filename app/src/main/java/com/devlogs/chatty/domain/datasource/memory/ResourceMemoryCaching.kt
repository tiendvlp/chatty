package com.devlogs.chatty.domain.datasource.memory

interface ResourceMemoryCaching {

    /**
     * @throws CommonErrorEntity.ResourceNotFoundErrorEntity
     * */
    fun getAvatar (userEmail: String) : ByteArray
}