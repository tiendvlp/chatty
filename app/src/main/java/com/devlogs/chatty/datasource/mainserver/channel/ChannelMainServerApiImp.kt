package com.devlogs.chatty.datasource.mainserver.channel

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.common.helper.simple
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.ChannelMainServerModel
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import mainserver.GetChannelsOverPeriodOfTimeQuery
import mainserver.GetPreviousChannelsQuery
import javax.inject.Inject

class ChannelMainServerApiImp : ChannelMainServerApi {
    private val mApolloClient : ApolloClient

    @Inject
    constructor(apolloClient: ApolloClient) {
        this.mApolloClient = apolloClient
    }

    override suspend fun getPreviousChannels(lastUpdate: Long, count: Int): List<ChannelMainServerModel> {
        try {
            val response = mApolloClient.query(GetPreviousChannelsQuery(lastUpdate,count)).await()

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

            val result: List<GetPreviousChannelsQuery.GetPreviousChannel?> = response.data?.getPreviousChannels?: throw NotFoundErrorEntity("Can not find channel")

            return result.map { channelResult ->

                if (channelResult == null) {
                    throw NotFoundErrorEntity("Channel is missing")
                }

                val channelStatus = ChannelMainServerModel.Status(channelResult.status.senderEmail, channelResult.status.type, channelResult.status.content)
                val channelMembers = channelResult.members.map { memberResult ->
                    ChannelMainServerModel.Member(memberResult!!.id, memberResult.email)
                }
                val seenPeople : List<String> = channelResult.seen.map { it!! }

                ChannelMainServerModel(
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

    override suspend fun getChannelsOverPeriodOfTime(from: Long, to: Long): List<ChannelMainServerModel> {
        try {
            val response = mApolloClient.query(GetChannelsOverPeriodOfTimeQuery(from,to)).await()

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

            val result: List<GetChannelsOverPeriodOfTimeQuery.GetChannelsOverPeriodOfTime?> = response.data?.getChannelsOverPeriodOfTime?: throw NotFoundErrorEntity("Can not find channel")

            return result.map { channelResult ->

                if (channelResult == null) {
                    throw NotFoundErrorEntity("Channel is missing")
                }

                val channelStatus = ChannelMainServerModel.Status(channelResult.status.senderEmail, channelResult.status.type, channelResult.status.content)
                val channelMembers = channelResult.members.map { memberResult ->
                    ChannelMainServerModel.Member(memberResult!!.id, memberResult.email)
                }
                val seenPeople : List<String> = channelResult.seen.map { it!! }

                ChannelMainServerModel(
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