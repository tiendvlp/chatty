package com.devlogs.chatty.realtime

import com.devlogs.chatty.common.base.BaseObservable
import com.devlogs.chatty.common.helper.getImageBytes
import com.devlogs.chatty.config.LOCALHOST
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelMemberEntity
import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity
import com.devlogs.chatty.realtime.ChannelRealtime.Listener
import io.socket.client.Socket
import org.json.JSONObject

class ChannelRealtime : BaseObservable<Listener> {
    interface Listener {
        fun onNewChannelCreated (newChannel: ChannelEntity)
    }

    private val socketInstance: Socket

    constructor(socketInstance: Socket) {
        this.socketInstance = socketInstance
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
                    avatar = getImageBytes("http://$LOCALHOST:3000/api/v1/avatar/download/$email")
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