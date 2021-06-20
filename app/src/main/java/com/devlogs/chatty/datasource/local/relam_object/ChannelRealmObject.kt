package com.devlogs.chatty.datasource.local.relam_object

import com.devlogs.chatty.domain.entity.channel.ChannelEntity
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

fun ChannelEntity.to () : ChannelRealmObject {
    val seenDb = RealmList<String>()
    seen.forEach {
        seenDb.add(it)
    }
    val memberDb = RealmList<ChannelMemberRealmObject>()
    members.forEach {
        memberDb.add(it.to())
    }

    return ChannelRealmObject(
        latestUpdate = this.latestUpdate,
        createdDate = this.createdDate,
        seen = seenDb,
        members = memberDb,
        status = status.to(),
        adminEmail = admin,
        title = title,
        id = id
    )
}
