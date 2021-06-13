package com.devlogs.chatty.common.application

object SharedMemory {
    var email: String? = null
    var name: String? = null

    fun setAccount (email: String, name: String) {
        SharedMemory.email = email
        SharedMemory.name = name
    }
}