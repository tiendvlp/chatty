package com.devlogs.chatty.datasource.local.relam_object

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

    constructor(
        id: String,
        channelId: String,
        type: String,
        content: String,
        senderEmail: String,
        createdDate: Long
    ) {

        this.id = id
        this.channelId = channelId
        this.type = type
        this.content = content
        this.senderEmail = senderEmail
        this.createdDate = createdDate
    }

    constructor()

}