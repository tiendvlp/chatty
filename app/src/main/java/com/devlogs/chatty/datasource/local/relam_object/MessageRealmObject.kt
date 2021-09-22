package com.devlogs.chatty.datasource.local.relam_object

import com.devlogs.chatty.domain.entity.message.MessageEntity
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class MessageRealmObject : RealmObject {
    @PrimaryKey
    var id: String?=null
    @Required
    var channelId: String?=null
    @Required
    var type: String?=null
    @Required
    var content: String?=null
    @Required
    var senderEmail: String?=null
    @Required
    var createdDate: Long?=null
    @Required
    var status: String? = null

    constructor(
        id: String,
        channelId: String,
        type: String,
        content: String,
        senderEmail: String,
        createdDate: Long,
        status: String
    ) {
        assert(status.equals("DONE") || status.equals("FAILED") || status.equals("SENDING"))
        this.status = status
        this.id = id
        this.channelId = channelId
        this.type = type
        this.content = content
        this.senderEmail = senderEmail
        this.createdDate = createdDate
    }

    constructor()

}

fun MessageEntity.to() : MessageRealmObject {
    val statusDb = when (status) {
        MessageEntity.Status.DONE -> {
            "DONE";
        }
        MessageEntity.Status.FAILED -> {
            "FAILED"
        }
        MessageEntity.Status.SENDING -> {
            "SENDING"
        }
    }

    return MessageRealmObject(id, channelId, type, content, senderEmail, createdDate, statusDb)
}