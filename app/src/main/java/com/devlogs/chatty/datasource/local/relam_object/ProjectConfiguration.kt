package com.devlogs.chatty.datasource.local.relam_object

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class ProjectConfiguration : RealmObject {
    @PrimaryKey
    var id: String? = null
    @Required
    var lastUpdateChannelTime: Long? = null

    constructor(lastUpdateChannel: Long) {
        id = "CONFIG"
        this.lastUpdateChannelTime = lastUpdateChannel
    }

    constructor() {
        id = "CONFIG"
    }

}