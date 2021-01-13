package com.devlogs.chatty.domain.datasource.mainserver

import com.devlogs.chatty.domain.error.CommonErrorEntity
import org.json.JSONObject

interface ChannelMainServerApi {
    /**
     * @throws CommonErrorEntity.NotFoundErrorEntity
     * @throws CommonErrorEntity.NetworkErrorEntity
     * @throws CommonErrorEntity.UnAuthorizedErrorEntity
     * */
    suspend fun getUserLatestChannels (count: Int) : List<ChannelModel>
    data class ChannelModel(
            val id: String,
            val title: String,
            val admin: String,
            val status: Status,
            val members: List<Member>,
            val seen: List<String>,
            val createdDate: Long,
            val latestUpdate: Long) {
        data class Status (val senderEmail: String, val description: Description) {
            data class Description (val type: String, val content: String)
        }
        data class Member (val id: String, val email: String, val name: String, val avatar: MemberAvatar)
        data class MemberAvatar (val type: String, val content: String)
    }
}