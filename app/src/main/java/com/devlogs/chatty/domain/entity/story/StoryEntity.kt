package com.devlogs.chatty.domain.entity.story

sealed class StoryEntity {
    val id : String
    val type : String
    val owner : String
    val upLoadedDate : Long
    val outDated: Boolean
    val channelId : String

    constructor(id : String, type: String, owner: String, upLoadedDate: Long, outDated: Boolean, channelId : String) {
        this.id = id
        this.type = type
        this.owner = owner
        this.upLoadedDate = upLoadedDate
        this.outDated = outDated
        this.channelId = channelId
    }

    class ImageStoryEntity : StoryEntity {
         val imageUrl : String

        constructor(id : String, type: String, owner: String, upLoadedDate: Long, outDated: Boolean, channelId : String, imageUrl : String) : super (id,type, owner, upLoadedDate, outDated, channelId) {
            this.imageUrl = imageUrl
        }
    }
}