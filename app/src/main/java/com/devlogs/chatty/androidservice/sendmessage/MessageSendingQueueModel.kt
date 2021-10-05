package com.devlogs.chatty.androidservice.sendmessage

data class MessageSendingQueueModel(
    val id: String?,
    val senderEmail: String, val content: String, val channelId: String) {


}