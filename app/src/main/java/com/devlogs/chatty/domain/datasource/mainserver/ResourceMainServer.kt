package com.devlogs.chatty.domain.datasource.mainserver

interface ResourceMainServer {
     fun getResourceUrl (fileName: String) : String
     fun getAvatarUrl (userEmail: String) : String
}