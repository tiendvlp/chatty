package com.devlogs.chatty.channel

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.datasource.local.process.ConfigurationLocalDbApi
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/** Execute when user want to reload all channels
 * How it works ?
 * Send a getChannelOverPeriodOfTime Request to server to all channel that has been updated from
 * the last time local db update to current time
 * the result that return from the server only fall into two situations:
 * 1. Channel does not exist => add that channel to localdb
 * 2. Channel already exist in localdb => update it
 * @Requirement: Make sure the localdb matches exactly with server db
 * @Return All channel (entity) that the server had returned.
 * */
class ReloadChannelUseCaseSync {

    sealed class Result {
        data class Success(val channels: List<ChannelEntity>) : Result()
        object GeneralError : Result()
        object NetworkError : Result()
        object UnAuthorized : Result()
    }

    private val localDbApi: ChannelLocalDbApi
    private val mainServerApi: ChannelMainServerApi
    private val loadMorePolicy: LoadMoreChannelPolicy
    private val projectConfigurationDb : ConfigurationLocalDbApi

    @Inject
    constructor(
        loadMorePolicy: LoadMoreChannelPolicy,
        mainServerApi: ChannelMainServerApi,
        localDbApi: ChannelLocalDbApi,
        projectConfigurationDb: ConfigurationLocalDbApi
    ) {

        this.loadMorePolicy = loadMorePolicy
        this.mainServerApi = mainServerApi
        this.localDbApi = localDbApi
        this.projectConfigurationDb = projectConfigurationDb
    }

    suspend fun execute(): Result = withContext(BackgroundDispatcher) {
        try {
            val lastUpdate = projectConfigurationDb.getChannelLastUpdateTime()
            val result: ArrayList<ChannelEntity> = ArrayList();
            val channelsFromServer = mainServerApi.getChannelsOverPeriodOfTime(lastUpdate, Date().time)
            result.addAll(channelsFromServer.map {
                it.toChannelEntity()
            })
            saveChannelsToDb(channelsFromServer)
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

    private suspend fun saveChannelsToDb (serverRespond : List<ChannelMainServerModel>) = withContext( BackgroundDispatcher) {
        launch (NonCancellable) {
            val channelMemberLocals : RealmList<ChannelMemberRealmObject> = RealmList()
            val channelSeen = RealmList<String>()
            localDbApi.overrideChannel(serverRespond.map {
                    channelModel ->
                channelMemberLocals.clear()
                channelSeen.clear()
                channelSeen.addAll(channelModel.seen)
                channelMemberLocals.addAll(channelModel.members.map {
                    ChannelMemberRealmObject(it.email, it.id)
                })

                ChannelRealmObject(
                    latestUpdate = channelModel.latestUpdate,
                    createdDate = channelModel.createdDate,
                    seen= channelSeen,
                    members = channelMemberLocals,
                    status = ChannelStatusRealmObject(channelModel.status.senderEmail, channelModel.status.content, channelModel.status.type),
                    adminEmail = channelModel.admin,
                    title = channelModel.title,
                    id = channelModel.id
                )
            })

            val lastUpdate = localDbApi.getLatestUpdateTime()
            projectConfigurationDb.setChannelLastUpdateTime(lastUpdate)
        }
    }
}