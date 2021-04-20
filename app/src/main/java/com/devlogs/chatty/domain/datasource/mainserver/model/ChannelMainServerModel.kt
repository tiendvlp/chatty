package com.devlogs.chatty.domain.datasource.mainserver.model

data class ChannelMainServerModel(
    val id: String,
    val title: String,
    val admin: String,
    val status: Status,
    val members: List<Member>,
    val seen: List<String>,
    val createdDate: Long,
    val latestUpdate: Long) {
    data class Status (val senderEmail: String, val type: String, val content: String)
    data class Member(val id: String, val email: String)
}