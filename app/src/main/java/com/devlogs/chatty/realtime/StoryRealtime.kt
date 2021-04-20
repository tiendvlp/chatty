package com.devlogs.chatty.realtime

import com.devlogs.chatty.common.base.BaseObservable
import com.devlogs.chatty.domain.entity.story.StoryEntity
import io.socket.client.Socket
import org.json.JSONObject
import javax.inject.Inject

class StoryRealtime : BaseObservable<StoryRealtime.Listener> {
    interface Listener {
        fun onNewStory(storyEntity: StoryEntity)
    }

    private val socketInstance: Socket

    @Inject
    constructor(socketInstance: Socket) {
        this.socketInstance = socketInstance
        startListen()
    }

    private fun startListen () {
        socketInstance.on("newStory") { args ->
            val storyJson = JSONObject(args[0].toString())
            val newStoryEntity = StoryEntity(
                id = storyJson.getString("_id"),
                type = storyJson.getString("type"),
                owner = storyJson.getString("owner"),
                upLoadedDate = storyJson.getLong("uploadedDate"),
                outDated = storyJson.getLong("outDatedDate"),
                channelId = storyJson.getString("channelId"),
                content =  storyJson.getString("content")
            )
            getListener().forEach {
                it.onNewStory(newStoryEntity)
            }
        }
    }
}