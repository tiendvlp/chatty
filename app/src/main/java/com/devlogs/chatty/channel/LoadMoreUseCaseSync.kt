package com.devlogs.chatty.channel

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.common.helper.warningLog
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.ChannelMemberRealmObject
import com.devlogs.chatty.datasource.local.relam_object.ChannelRealmObject
import com.devlogs.chatty.datasource.local.relam_object.ChannelStatusRealmObject
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.ChannelMainServerModel
import com.devlogs.chatty.domain.datasource.mapper.toChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity
import io.realm.RealmList
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * @Excute: when ever the user want to load more channel (the rule of load more is defined in LoadMorePolicy.kt)
 * There are 2 main step:
 * Fist: Load channel inside local db first
 * Check if the amount of loaded channels (by LoadMorePolicy.kt class ) if not:
 * Send the getPreviousChannelWithCount request to server (the count is LoadMorePolicy.requiredAmount - currentLoadedChannelAmount)
 * Save all channels that returned from getPreviousChannelWithCount req into localdb
 * */

class LoadMoreUseCaseSync {

    sealed class Result {
        data class Success(val channels: List<ChannelEntity>) : Result()
        object  Empty : Result()
        object GeneralError : Result()
        object NetworkError : Result()
        object UnAuthorized : Result()
    }

    private val localDbApi: ChannelLocalDbApi
    private val mainServerApi: ChannelMainServerApi
    private val loadMorePolicy: LoadMoreChannelPolicy

    @Inject
    constructor(channelLocalDbApi: ChannelLocalDbApi, channelMainServerApi: ChannelMainServerApi) {
        this.localDbApi = channelLocalDbApi
        this.mainServerApi = channelMainServerApi
        this.loadMorePolicy = LoadMoreChannelPolicyImp()
    }

    suspend fun execute(): Result = withContext(BackgroundDispatcher) {
        try {
            val result = ArrayList<ChannelEntity>()
            val lastUpdateTime = localDbApi.getLatestLastUpdateTime()
            normalLog("lastUpdateTime: $lastUpdateTime")
            val channelsLoadMoreFromServer =
                mainServerApi.getPreviousChannels(
                    lastUpdateTime,
                    loadMorePolicy.getMaxNumberOfChannel())
            if (channelsLoadMoreFromServer.isEmpty()) {
                warningLog("No more channels")
                return@withContext Result.Empty
            }
                result.addAll(channelsLoadMoreFromServer.map { it.toChannelEntity() })
                saveChannelsToDb(channelsLoadMoreFromServer)
            Result.Success(result)
        } catch (e: CommonErrorEntity.NetworkErrorEntity) {
            Result.NetworkError
        } catch (e: CommonErrorEntity.NotFoundErrorEntity) {
            // Not found mean that the result is NULL not EMPTY_ARRAY
            Result.GeneralError
        } catch (e: CommonErrorEntity.GeneralErrorEntity) {
            Result.GeneralError
        } catch (e: AuthenticationErrorEntity) {
            Result.UnAuthorized
        }
    }

    private suspend fun saveChannelsToDb(serverRespond: List<ChannelMainServerModel>) =
            CoroutineScope(BackgroundDispatcher).launch  {
                val channelMemberLocals: RealmList<ChannelMemberRealmObject> = RealmList()
                val channelSeen = RealmList<String>()
                localDbApi.addChannel(serverRespond.map { channelModel ->
                    channelMemberLocals.clear()
                    channelSeen.clear()
                    channelSeen.addAll(channelModel.seen)
                    channelMemberLocals.addAll(channelModel.members.map {
                        ChannelMemberRealmObject(it.email, it.id)
                    })

                    ChannelRealmObject(
                        channelModel.latestUpdate,
                        channelModel.createdDate,
                        channelSeen,
                        channelMemberLocals,
                        ChannelStatusRealmObject(
                            channelModel.status.senderEmail,
                            channelModel.status.content,
                            channelModel.status.type
                        ),
                        channelModel.admin,
                        channelModel.title,
                        channelModel.id
                    )
                })
        }
}