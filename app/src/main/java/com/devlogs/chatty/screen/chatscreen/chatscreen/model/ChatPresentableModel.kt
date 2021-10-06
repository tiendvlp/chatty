package com.devlogs.chatty.screen.chatscreen.chatscreen.model

import com.devlogs.chatty.domain.entity.message.MessageEntity

enum class ChatType {
    URL, TEXT, VIDEO, IMAGE
}

enum class ChatState {
    SENDING, FAILED, SENT
}


data class ChatPresentableModel(
    var id: String,
    var type: ChatType,
    var senderEmail: String,
    var content: String,
    var createdDate: Long,
    var state:ChatState
) : Comparable<ChatPresentableModel> {
    override fun compareTo(other: ChatPresentableModel): Int {
        if (other.id.equals(id)) {
            return 0
        }
        if (createdDate < other.createdDate) {
            return -1
        }
        return 1
    }
}

fun MessageEntity.to () : ChatPresentableModel {
    var chatType : ChatType? = ChatType.TEXT

    if (type.equals("text", ignoreCase = true)) {
        chatType = ChatType.TEXT
    }

    var chatState = when (status) {
        MessageEntity.Status.FAILED -> ChatState.FAILED
        MessageEntity.Status.SENDING -> ChatState.SENDING
        MessageEntity.Status.DONE -> ChatState.SENT
    }

    return ChatPresentableModel(
        id, chatType!!, senderEmail, content, createdDate, chatState
    )
}