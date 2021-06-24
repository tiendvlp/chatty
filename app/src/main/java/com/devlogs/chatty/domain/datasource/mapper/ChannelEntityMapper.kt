package com.devlogs.chatty.domain.datasource.mapper

import com.devlogs.chatty.datasource.local.relam_object.ChannelRealmObject
import com.devlogs.chatty.domain.datasource.mainserver.model.ChannelMainServerModel
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelMemberEntity
import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity

fun ChannelRealmObject.toChannelEntity () : ChannelEntity {
    return ChannelEntity(
        id = this.id!!,
        title = this.title!!,
        admin = this.adminEmail!!,
        createdDate = this.createdDate!!,
        latestUpdate = this.latestUpdate!!,
        status = ChannelStatusEntity(this.status!!.senderEmail!!, this.status!!.content!!, this.status!!.type!!),
        members = this.members!!.map {
            ChannelMemberEntity(
                it.id!!,
                it.email!!,
            )
        },
        seen = this.seen!!
    )
}

fun ChannelMainServerModel.toChannelEntity () : ChannelEntity {
   val channelMembers : List<ChannelMemberEntity> = this.members.map { memberModel ->
            ChannelMemberEntity(memberModel.id, memberModel.email)
        }

        val channelStatus = ChannelStatusEntity(
            this.status.senderEmail,
            this.status.content,
            this.status.type,
        )

        return ChannelEntity(this.id, this.title, this.admin, channelStatus, channelMembers, this.seen, this.createdDate, this.latestUpdate)
}