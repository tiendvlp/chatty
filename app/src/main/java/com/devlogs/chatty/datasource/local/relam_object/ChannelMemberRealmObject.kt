package com.devlogs.chatty.datasource.local.relam_object

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class ChannelMemberRealmObject : RealmObject {
    // this id is not the primary key because it reflects the users as a channels' members, a user can join multiple channels
    var id: String? = null
    @Required
    var email: String? = null
    var avatar: ByteArray? = null

    constructor()

    constructor(email: String, id: String, avatar:ByteArray) {
        this.email = email
        this.id = id
        this.avatar = avatar
    }
}