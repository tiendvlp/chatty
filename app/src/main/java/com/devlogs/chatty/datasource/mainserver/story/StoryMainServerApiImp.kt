package com.devlogs.chatty.datasource.mainserver.story

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.common.helper.simple
import com.devlogs.chatty.domain.datasource.mainserver.StoryMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.ChannelMainServerModel
import com.devlogs.chatty.domain.datasource.mainserver.model.StoryMainServerModel
import com.devlogs.chatty.domain.error.CommonErrorEntity
import mainserver.GetPreviousChannelsQuery
import mainserver.GetPreviousStoryWithCountQuery
import mainserver.GetStoryOverPeriodOfTimeQuery
import javax.inject.Inject

class StoryMainServerApiImp : StoryMainServerApi{

    private val mApolloClient : ApolloClient

    @Inject
    constructor(apolloClient: ApolloClient) {
        this.mApolloClient = apolloClient
    }

    override suspend fun getPreviousStories(since: Long, count: Int): List<StoryMainServerModel> {
        try {
            val response = mApolloClient.query(GetPreviousStoryWithCountQuery(since,count)).await()

            if (response.hasErrors()) {
                val currentError = response.errors!![0].simple()
                currentError.code.let {
                    if (currentError.code == 404) {
                        throw CommonErrorEntity.NotFoundErrorEntity("Can not find channel")
                    }
                }
                normalLog("Error happened: " + currentError.message)
                throw CommonErrorEntity.GeneralErrorEntity("Internal server error")
            }

            val result: List<GetPreviousStoryWithCountQuery.GetPreviousStoryWithCount?> = response.data?.getPreviousStoryWithCount?: throw CommonErrorEntity.NotFoundErrorEntity(
                "Can not find channel"
            )

            return result.map { storyResult ->
                if (storyResult==null) {throw CommonErrorEntity.NotFoundErrorEntity("Story not found")}

                StoryMainServerModel(
                    storyResult.id,
                    storyResult.channelId,
                    storyResult.content,
                    storyResult.type,
                    storyResult.owner,
                    storyResult.uploadedDate.toLong(),
                    storyResult.outdatedDate.toString().toLong()
                )
            }
        } catch (e: ApolloException) {
            throw CommonErrorEntity.NetworkErrorEntity(e.message ?: "")
        }
    }

    override suspend fun getStoriesOverPeriodOfTime(
        from: Long,
        to: Long
    ): List<StoryMainServerModel> {
        try {
            val response = mApolloClient.query(GetStoryOverPeriodOfTimeQuery(from,to)).await()

            if (response.hasErrors()) {
                val currentError = response.errors!![0].simple()
                currentError.code.let {
                    if (currentError.code == 404) {
                        throw CommonErrorEntity.NotFoundErrorEntity("Can not find channel")
                    }
                }
                normalLog("Error happened: " + currentError.message)
                throw CommonErrorEntity.GeneralErrorEntity("Internal server error")
            }

            val result: List<GetStoryOverPeriodOfTimeQuery.GetStoryOverPeriodOfTime?> = response.data?.getStoryOverPeriodOfTime?: throw CommonErrorEntity.NotFoundErrorEntity(
                "Can not find channel"
            )

            return result.map { storyResult ->
                if (storyResult==null) {throw CommonErrorEntity.NotFoundErrorEntity("Story not found")}

                StoryMainServerModel(
                    storyResult.id,
                    storyResult.channelId,
                    storyResult.content,
                    storyResult.type,
                    storyResult.owner,
                    storyResult.uploadedDate.toLong(),
                    storyResult.outdatedDate.toString().toLong()
                )
            }
        } catch (e: ApolloException) {
            throw CommonErrorEntity.NetworkErrorEntity(e.message ?: "")
        }
    }
}