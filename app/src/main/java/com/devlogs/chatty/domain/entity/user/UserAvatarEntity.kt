package com.devlogs.chatty.domain.entity.user

sealed class UserAvatarEntity {
    val type : String

    constructor(type : String) {
        this.type = type
    }

    class LocalAvatar : UserAvatarEntity {
         val avatarName : String
         val avatarColor : List<Float>

        constructor(type: String, avatarName : String, avatarColor : List<Float>) : super (type) {
            this.avatarColor = avatarColor
            this.avatarName = avatarName
        }
    }
}