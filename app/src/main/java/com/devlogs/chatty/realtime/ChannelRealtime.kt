package com.devlogs.chatty.realtime

import com.devlogs.chatty.common.base.BaseObservable
import com.devlogs.chatty.common.helper.getImageBytes
import com.devlogs.chatty.common.helper.getUserAvatar
import com.devlogs.chatty.common.mapper.Mapper
import com.devlogs.chatty.config.LOCALHOST
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelMemberEntity
import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity
import com.devlogs.chatty.realtime.ChannelRealtime.Listener
import io.socket.client.Socket
import org.json.JSONObject
import javax.inject.Inject

class ChannelRealtime : BaseObservable<Listener> {
    interface Listener {
        fun onNewChannelCreated (newChannel: ChannelEntity)
    }

    private val socketInstance: Socket
    @Inject
    lateinit var channelLocalDbApi: ChannelLocalDbApi

    constructor(socketInstance: Socket) {
        this.socketInstance = socketInstance
        startListen()
    }

    private fun startListen () {
        socketInstance.on("newChannel") { args ->
            val jsonData = JSONObject(args[0].toString())
            val jsonDataMembers = jsonData.getJSONArray("members")
            var currentMember : JSONObject? = null

            val channelMembers = MutableList(jsonDataMembers.length()) { currentIndex ->
                currentMember = jsonDataMembers.getJSONObject(currentIndex)
                val email = currentMember!!.getString("email")
                ChannelMemberEntity(
                    id = currentMember!!.getString("id"),
                    email = email,
                )
            }

            val statusJson = jsonData.getJSONObject("status")
            val channelStatus = ChannelStatusEntity (
                senderEmail = statusJson.getString("senderEmail"),
                content = statusJson.getString("content"),
                type = statusJson.getString("type")
                )

            val seenJson = jsonData.getJSONArray("seen")
            val seen = MutableList(seenJson.length()) {index ->
                seenJson.getString(index)
            }

            val newChannelEntity = ChannelEntity(
                id = jsonData.getString("_id"),
                title = jsonData.getString("title"),
                members = channelMembers,
                admin = jsonData.getString("admin"),
                status = channelStatus,
                seen = seen,
                createdDate = jsonData.getLong("createdDate"),
                latestUpdate = jsonData.getLong("latestUpdate")
            )

            getListener().forEach {
                it.onNewChannelCreated(newChannelEntity)
            }
        }
    }

}