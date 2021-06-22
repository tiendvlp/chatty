package com.devlogs.chatty.screen.chatscreen.chatscreen.model

enum class ChatType {
    URL, TEXT, VIDEO, IMAGE
}

data class ChatPresentableModel(
    val id: String,
    val type: ChatType,
    val senderEmail: String,
    val content: String,
    val createdDate: Long
)