package com.devlogs.chatty.channel

import com.devlogs.chatty.channel.GetBeforeUserChannelsWithCountUseCaseSync.Result.*
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.ChannelMemberRealmObject
import com.devlogs.chatty.datasource.local.relam_object.ChannelRealmObject
import com.devlogs.chatty.datasource.local.relam_object.ChannelStatusRealmObject
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelMemberEntity
import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import io.realm.RealmList
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetBeforeUserChannelsWithCountUseCaseSync {
    sealed class Result {
        data class Success (val channels : List<ChannelEntity>) : Result()
        object GeneralError : Result()
        object NetworkError : Result ()
        object InvalidRefreshToken : Result()
    }

    private val mChannelMainSerApi: ChannelMainServerApi
    private val channelLocalDbApi: ChannelLocalDbApi

    @Inject
    constructor(channelMainServerApi: ChannelMainServerApi, channelLocalDbApi: ChannelLocalDbApi) {
        this.channelLocalDbApi = channelLocalDbApi
        mChannelMainSerApi = channelMainServerApi
    }

    suspend fun execute (since: Long, count: Int) : Result = withContext(BackgroundDispatcher) {
        try {
            val response = mChannelMainSerApi.getPreviousChannels(since, count)
            val channelMemberLocalModels = RealmList<ChannelMemberRealmObject>();
            val channelMemberEntities = ArrayList<ChannelMemberEntity>();
            val channels : List<ChannelEntity> = response.map { channelModel ->
                for (member in channelModel.members) {
                    channelMemberEntities.add(ChannelMemberEntity(member.id, member.email))
                    channelMemberLocalModels.add(ChannelMemberRealmObject(member.id, member.email))
                }
                val seenLocal = RealmList<String>()
                seenLocal.addAll(channelModel.seen)

                channelLocalDbApi.addChannel(
                    ChannelRealmObject(
                    channelModel.latestUpdate,
                    channelModel.createdDate,
                        seenLocal,
                        channelMemberLocalModels,
                    ChannelStatusRealmObject(channelModel.status.senderEmail, channelModel.status.content, channelModel.status.type),
                    channelModel.admin,
                    channelModel.title,
                    channelModel.id
                    )
                )
                val channelStatus = ChannelStatusEntity(
                    channelModel.status.senderEmail,
                    channelModel.status.type,
                    channelModel.status.content
                )
                ChannelEntity(channelModel.id, channelModel.title, channelModel.admin, channelStatus, channelMemberEntities, channelModel.seen, channelModel.createdDate, channelModel.latestUpdate)
            }

            Success(channels)
        } catch (e: NetworkErrorEntity) {
            NetworkError
        } catch (e: NotFoundErrorEntity) {
            // Not found mean that the result is NULL not EMPTY_ARRAY
            GeneralError
        } catch (e: GeneralErrorEntity) {
            GeneralError
        } catch (e: AuthenticationErrorEntity) {
            InvalidRefreshToken
        }
    }

}