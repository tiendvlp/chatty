package com.devlogs.chatty.channel

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import kotlinx.coroutines.withContext

/** Execute when user want to reload all channels
* How it works ?
* Send a getChannelOverPeriodOfTime Request to server to all channel that has been updated from
* the last time local db update to current time
* the result that return from the server only fall into two situations:
* 1. Channel does not exist => add that channel to localdb
* 2. Channel already exist in localdb => update it
* @Requirement: Make sure the localdb matches exactly with server db
* @Return All channel (entity) that the server had returned.
* */
class ReloadChannelUseCaseSync {
    suspend fun execute () : List<ChannelEntity> = withContext(BackgroundDispatcher) {
        emptyList()
    }
}