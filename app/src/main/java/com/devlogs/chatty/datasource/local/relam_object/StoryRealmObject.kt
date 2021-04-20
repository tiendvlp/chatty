package com.devlogs.chatty.datasource.local.relam_object

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class StoryRealmObject : RealmObject {
    @PrimaryKey
    var id: String? = null
    @Required
    var type: String? = null
    @Required
    var owner: String? = null
    @Required
    var uploadedDate: Long? = null
    @Required
    var outDated: Long? = null
    @Required
    var channelId: String? = null
    @Required
    var content: String? = null
    constructor()

    constructor(
        id: String,
        type: String,
        owner: String,
        uploadedDate: Long,
        outDated: Long,
        channelId: String,
        content: String
    ) {
        this.id = id
        this.type = type
        this.owner = owner
        this.uploadedDate = uploadedDate
        this.outDated = outDated
        this.channelId = channelId
        this.content = content
    }
}