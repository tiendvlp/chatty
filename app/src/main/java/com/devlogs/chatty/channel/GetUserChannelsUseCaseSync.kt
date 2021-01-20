package com.devlogs.chatty.channel

import com.devlogs.chatty.channel.GetUserChannelsUseCaseSync.Result.*
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.ChannelMainServerApi.ChannelModel
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.domain.entity.channel.ChannelMemberEntity
import com.devlogs.chatty.domain.entity.channel.ChannelStatusEntity
import com.devlogs.chatty.domain.entity.user.UserAvatarEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class GetUserChannelsUseCaseSync {
    sealed class Result {
        data class Success (val channels : List<ChannelEntity>) : Result()
        object GeneralError : Result()
        object NetworkError : Result ()
        object InvalidRefreshToken : Result()
    }

    private val mChannelMainSerApi: ChannelMainServerApi

    @Inject
    constructor(channelMainServerApi: ChannelMainServerApi) {
        mChannelMainSerApi = channelMainServerApi
    }

    /**
     * @throws UnSupportedDataType when get an avatar that this client do not support
     * */
    suspend fun execute (count: Int) : Result = withContext(BackgroundDispatcher){
        try {

            val response = mChannelMainSerApi.getUserLatestChannels(count)
            val channels : List<ChannelEntity> = response.map {channelModel ->
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

    private fun convertToEntity (channelModel: ChannelModel) : ChannelEntity {
        val channelMembers : List<ChannelMemberEntity> = channelModel.members.map { memberModel ->
            ChannelMemberEntity(memberModel.id, memberModel.email, memberModel.name, getAvatar(memberModel.avatar))
        }

        val channelStatus = ChannelStatusEntity(
                channelModel.status.senderEmail,
                ChannelStatusEntity.ChannelStatusDescription(
                        channelModel.status.description.type,
                        channelModel.status.description.content
                )
        )

        return ChannelEntity(channelModel.id, channelModel.title, channelModel.admin, channelStatus, channelMembers, channelModel.seen, channelModel.createdDate, channelModel.latestUpdate)
    }

    private fun getAvatar (avatarModel: ChannelModel.MemberAvatar) : UserAvatarEntity {
        when (avatarModel.type) {
            "LOCAL" -> {
                return getLocalAvatar(avatarModel.type, avatarModel.content)
            }
        }

        throw UnSupportedDataType("Your avatar type: ${avatarModel.type} is not supported")
    }

    private fun getLocalAvatar (type: String, content: String) : UserAvatarEntity {
        if (type != "LOCAL") { throw UnSupportedDataType("Your avatar type: $type is not supported") }

        val localContent = JSONObject(content)
        val avatarName = localContent.getString("avatarName")

        val avatarColor = ArrayList<Float>()
        val avatarJsonColor = localContent.getJSONArray("avatarColor")

        for (i in 0..avatarJsonColor.length()-1) {
            avatarColor.add(avatarJsonColor.getLong(i).toFloat())
        }

        return UserAvatarEntity.LocalAvatar(type,avatarName, avatarColor)

    }

}