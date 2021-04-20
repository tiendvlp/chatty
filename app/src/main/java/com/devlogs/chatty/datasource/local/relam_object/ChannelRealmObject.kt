package com.devlogs.chatty.datasource.local.relam_object

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class ChannelRealmObject : RealmObject {
    @PrimaryKey
    var id: String?=null
    @Required
    var title: String?=null
    @Required
    var adminEmail: String?=null
    var status: ChannelStatusRealmObject?=null
    var members: RealmList<ChannelMemberRealmObject>?=null
    var seen: RealmList<String>?=null
    @Required
    var createdDate: Long?=null
    @Required
    var latestUpdate: Long?=null

    constructor(
        latestUpdate: Long,
        createdDate: Long,
        seen: RealmList<String>,
        members: RealmList<ChannelMemberRealmObject>,
        status: ChannelStatusRealmObject,
        adminEmail: String,
        title: String,
        id: String
    ) {

        this.latestUpdate = latestUpdate
        this.createdDate = createdDate
        this.seen = seen
        this.members = members
        this.status = status
        this.adminEmail = adminEmail
        this.title = title
        this.id = id
    }

    constructor() {}
}