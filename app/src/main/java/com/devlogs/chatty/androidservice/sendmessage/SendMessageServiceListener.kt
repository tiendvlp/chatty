package com.devlogs.chatty.androidservice.sendmessage

import com.devlogs.chatty.domain.entity.message.MessageEntity

interface SendMessageServiceListener {
    fun messageSending (messageEntity: MessageEntity)
    fun messageSendFailed (messageEntity: MessageEntity)
    fun messageSendSuccess (messageEntity: MessageEntity)
}