package com.devlogs.chatty.datasource.room.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TokenDao {
    fun setRefreshToken ()
    fun removeRefreshToken ()
    fun getAccessToken ()
    fun getRefreshToken ()
}