package com.devlogs.chatty.channel

import com.devlogs.chatty.channel.GetUserChannelsOverPeriodOfTimeUseCaseSync.Result.*
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.getUserAvatar
import com.devlogs.chatty.datasource.local.process.ChannelLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.ChannelMemberRealmObject
import com.devlogs.chatty.datasource.local.relam_object.ChannelRealmObject
import com.devlogs.chatty.datasource.local.relam_object.ChannelStatusRealmObject
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.ChannelMainServerModel
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelMemberEntity
import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import io.realm.RealmList
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserChannelsOverPeriodOfTimeUseCaseSync {
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

    suspend fun execute (from: Long, to: Long) : Result = withContext(BackgroundDispatcher){
        try {
            val response = mChannelMainSerApi.getChannelsOverPeriodOfTime(from, to)
            val channelMemberLocals : RealmList<ChannelMemberRealmObject> = RealmList()
            val channelSeen = RealmList<String>();
            val channels : List<ChannelEntity> = response.map {channelModel ->
                channelMemberLocals.clear()
                channelSeen.clear()
                channelSeen.addAll(channelModel.seen)
                channelMemberLocals.addAll(channelModel.members.map { ChannelMemberRealmObject(it.email, it.id, getUserAvatar(it.email)) })

                channelLocalDbApi.addChannel(ChannelRealmObject(
                    channelModel.latestUpdate,
                    channelModel.createdDate,
                    channelSeen,
                    channelMemberLocals,
                    ChannelStatusRealmObject(channelModel.status.senderEmail, channelModel.status.content, channelModel.status.type),
                    channelModel.admin,
                    channelModel.title,
                    channelModel.id
                ))
                convertToEntity(channelModel)
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

    private fun convertToEntity (channelModel: ChannelMainServerModel) : ChannelEntity {
        val channelMembers : List<ChannelMemberEntity> = channelModel.members.map { memberModel ->
            ChannelMemberEntity(memberModel.id, memberModel.email, getUserAvatar(memberModel.email))
        }

        val channelStatus = ChannelStatusEntity(
                channelModel.status.senderEmail,
                channelModel.status.type,
                channelModel.status.content
        )

        return ChannelEntity(channelModel.id, channelModel.title, channelModel.admin, channelStatus, channelMembers, channelModel.seen, channelModel.createdDate, channelModel.latestUpdate)
    }
}