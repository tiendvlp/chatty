package com.devlogs.chatty.datasource.local.relam_object

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class ProjectConfiguration : RealmObject {
    @PrimaryKey
    var id: String? = null
    @Required
    var lastUpdateChannelTime: Long? = null
    @Required
    var lastUpdateMessageTime: Long? = null

    constructor(lastUpdateChannel: Long?, lastUpdateMessageTime: Long?) {
        id = "CONFIG"
        this.lastUpdateChannelTime = lastUpdateChannel
        this.lastUpdateMessageTime = lastUpdateMessageTime
    }

    constructor() {
        id = "CONFIG"
    }

}