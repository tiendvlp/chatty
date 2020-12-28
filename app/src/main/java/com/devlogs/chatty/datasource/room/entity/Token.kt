package com.devlogs.chatty.datasource.room.entity

import androidx.room.Entity

@Entity(tableName = "token_table")
data class Token (val accessToken:String, val refreshToken: String)