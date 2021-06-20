package com.devlogs.chatty.datasource.local.relam_object

import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity
import io.realm.RealmObject
import io.realm.annotations.Required

open class ChannelStatusRealmObject : RealmObject {
    @Required
    var senderEmail: String?=null
    @Required
    var content: String?=null
    @Required
    var type: String?=null

    constructor(senderEmail: String, content: String, type: String) {
        this.senderEmail = senderEmail
        this.content = content
        this.type = type
    }
    constructor()

}

fun ChannelStatusEntity.to() : ChannelStatusRealmObject
= ChannelStatusRealmObject(senderEmail, content, type)