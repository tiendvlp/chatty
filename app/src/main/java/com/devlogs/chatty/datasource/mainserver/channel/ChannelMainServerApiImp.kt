package com.devlogs.chatty.datasource.mainserver.channel

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.toJson
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.common.helper.simple
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi.ChannelModel
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mainserver.GetChannelsQuery
import javax.inject.Inject

class ChannelMainServerApiImp : ChannelMainServerApi {
    private val mApolloClient : ApolloClient

    @Inject
    constructor(apolloClient: ApolloClient) {
        this.mApolloClient = apolloClient
    }

    override suspend fun getChannels(lastUpdate: Long, count: Int): List<ChannelModel> {
        try {
            val response = mApolloClient.query(GetChannelsQuery(lastUpdate,count)).await()

            if (response.hasErrors()) {
                val currentError = response.errors!![0].simple()
                currentError.code.let {
                    if (currentError.code == 404) {
                        throw NotFoundErrorEntity("Can not find channel")
                    }
                }
                normalLog("Error happened: " + currentError.message)
                throw GeneralErrorEntity("Internal server error")
            }

            val result: List<GetChannelsQuery.GetChannel?> = response.data?.getChannels?: throw NotFoundErrorEntity("Can not find channel")

            return result.map { channelResult ->

                if (channelResult == null) {
                    throw NotFoundErrorEntity("Channel is missing")
                }

                val channelStatusDescription = ChannelModel.Status.Description(channelResult.status.__typename, channelResult.status.senderEmail)
                val channelStatus = ChannelModel.Status(channelResult.status.senderEmail, channelStatusDescription)
                val channelMembers = channelResult.members.map { memberResult ->
                    ChannelModel.Member(memberResult!!.id, memberResult.email, memberResult.name)
                }
                val seenPeople : List<String> = channelResult.seen.map { it!! }

                ChannelModel(
                        channelResult.id, channelResult.title,
                        channelResult.admin,channelStatus,
                        channelMembers, seenPeople,
                        channelResult.createdDate.toString().toLong(),
                        channelResult.latestUpdate.toString().toLong())
            }
        } catch (e: ApolloException) {
            throw NetworkErrorEntity(e.message?: "")
        }
    }
}