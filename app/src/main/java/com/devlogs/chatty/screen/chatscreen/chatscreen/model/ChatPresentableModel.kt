package com.devlogs.chatty.screen.chatscreen.chatscreen.model

import com.devlogs.chatty.domain.entity.message.MessageEntity

enum class ChatType {
    URL, TEXT, VIDEO, IMAGE
}

data class ChatPresentableModel(
    val id: String,
    val type: ChatType,
    val senderEmail: String,
    val content: String,
    val createdDate: Long
) : Comparable<ChatPresentableModel> {
    override fun compareTo(other: ChatPresentableModel): Int {
        if (other.id.equals(id)) {
            return 0
        }
        if (other.createdDate < createdDate) {
            return -1
        }
        return 1
    }
}

fun MessageEntity.to () : ChatPresentableModel {
    var chatType : ChatType? = null

    if (type.equals("text", ignoreCase = true)) {
        chatType = ChatType.TEXT
    }

    return ChatPresentableModel(
        id, chatType!!, senderEmail, content, createdDate
    )
}