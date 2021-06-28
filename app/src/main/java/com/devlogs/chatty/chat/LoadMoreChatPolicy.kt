package com.devlogs.chatty.chat


interface LoadMoreChatPolicy {
     fun getMaxNumberOfMessage(): Int
}