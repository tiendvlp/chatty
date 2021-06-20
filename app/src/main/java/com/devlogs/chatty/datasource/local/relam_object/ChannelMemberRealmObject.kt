package com.devlogs.chatty.datasource.local.relam_object

import com.devlogs.chatty.domain.entity.channel.ChannelMemberEntity
import io.realm.RealmObject
import io.realm.annotations.Required

open class ChannelMemberRealmObject : RealmObject {
    // this id is not the primary key because it reflects the users as a channels' members, a user can join multiple channels
    var id: String? = null
    @Required
    var email: String? = null

    constructor()

    constructor(email: String, id: String) {
        this.email = email
        this.id = id
    }
}

fun ChannelMemberEntity.to() : ChannelMemberRealmObject {
    return ChannelMemberRealmObject(email, id)
}