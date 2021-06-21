package com.devlogs.chatty.screen.mainscreen.channelscreen.model

import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import kotlinx.coroutines.withContext

 class ChannelPresentationModel(
    var title: String,
    var message: String,
    var sender: String,
    var lastUpdate: Long,
    var memberEmails: Array<String>,
    var displayedElement : Array<String>,
    var id: String,

): Comparable<ChannelPresentationModel> {
     override fun compareTo(other: ChannelPresentationModel): Int {
         if (other.id.equals(id)) {
             return 0
         }

         if (other.lastUpdate < lastUpdate) {
             return -1
         }

         return 1
     }

 }

// it's too heavy to run on MainThread
suspend fun ChannelEntity.to () : ChannelPresentationModel = withContext(BackgroundDispatcher) {
    var title = title
    var senderName = status.senderEmail.split('@')[0]
    if (status.senderEmail.equals(SharedMemory.email)) {
        senderName = "you"
    }
    if (title.isBlank()) {
        title = members.first { !it.email.equals(SharedMemory.email) }.email.split('@')[0]
    }
     ChannelPresentationModel(
        title = title,
        sender = senderName,
        message = status.content,
        id = id,
        lastUpdate = latestUpdate,
             memberEmails = members.map { it.email }.toTypedArray(),
             displayedElement = members.filter { it.email != admin }.map { it.email }.toTypedArray()
    )
}

suspend fun List<ChannelEntity>.to () : List<ChannelPresentationModel> {
    return map {
        it.to()
    }
}