package com.devlogs.chatty.domain.datasource.mainserver.model

sealed class UserAvatarMainServerModel {
    val type : String

    constructor(type : String) {
        this.type = type
    }

    class LocalAvatar : UserAvatarMainServerModel {
        val avatarName : String
        val avatarColor : List<Float>

        constructor(type: String, avatarName : String, avatarColor : List<Float>) : super (type) {
            this.avatarColor = avatarColor
            this.avatarName = avatarName
        }
    }
}