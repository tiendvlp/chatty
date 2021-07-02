package com.devlogs.chatty.screen.mainscreen.channelscreen.state

import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.presentationmodel.UserPresentationModel
import com.devlogs.chatty.screen.common.presentationstate.*
import com.devlogs.chatty.screen.common.presentationstate.CommonPresentationAction.InitAction
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction.*
import java.util.*

sealed class ChannelScreenPresentationState : PresentationState {
    override val allowSave: Boolean = false
    override fun getTag(): String {
        return "ChannelScreenPresentationState"
    }

    object LoadingState : ChannelScreenPresentationState() {
        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): CauseAndEffect {
            when (action) {
                is InitAction -> {
                    return CauseAndEffect(action,LoadingState)
                }
                is LoadChannelFailedAction -> {
                    return CauseAndEffect(action, ErrorState(action.errMessage))
                }
                is LoadChannelSuccessAction -> {
                    normalLog("LoadChannelSuccessAction occurs")
                    return CauseAndEffect(action, DisplayState(action.data))
                }
                is CancelAction -> {
                    return CauseAndEffect(action, ErrorState("Canceled"))
                }
            }
            return super.consumeAction(previousState, action)
        }
    }

    data class ErrorState(val errMessage: String) : ChannelScreenPresentationState() {
        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): CauseAndEffect {
            when (action) {
                is LoadAction -> {
                    return CauseAndEffect(action, LoadingState)
                }
            }
            return super.consumeAction(previousState, action)
        }
    }


    data class DisplayState(val channels: TreeSet<ChannelPresentationModel>, val user: UserPresentationModel? = null, var scrollPosition: Int = 0) :
        ChannelScreenPresentationState() {
        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): CauseAndEffect {
            when (action) {
                is NewChannelAction -> return CauseAndEffect(action, copy(channels = appendChannels(action.data)))
                is ChannelUpdatedAction -> return CauseAndEffect(action, copy(channels = replaceChannel(action.data)))
                is LoadMoreChannelAction -> return CauseAndEffect(action, copy())
                is LoadMoreChannelSuccessAction -> {
                    return CauseAndEffect(action, copy(channels = appendChannels(action.data)))
                }
                is LoadMoreChannelFailedAction -> {return CauseAndEffect(action, copy())}

                is ReLoadChannelSuccessAction -> {
                    return CauseAndEffect(action, copy(channels = appendChannels(action.data)))
                }
                is ReloadChannelFailedAction -> {
                    return CauseAndEffect(action, copy())
                }

                is LoadUserSuccessAction -> return CauseAndEffect(action, copy(user=action.user))
                is LoadUserFailedAction -> return CauseAndEffect(action, copy())
            }
            return super.consumeAction(previousState, action)
        }

        private fun appendChannels(addedChannels: TreeSet<ChannelPresentationModel>): TreeSet<ChannelPresentationModel> {
            val newChannels = TreeSet<ChannelPresentationModel>()
            newChannels.addAll(channels)
            newChannels.addAll(addedChannels)
            return newChannels
        }

        private fun replaceChannel (newChannel: ChannelPresentationModel): TreeSet<ChannelPresentationModel> {
            val newChannels = TreeSet<ChannelPresentationModel>()
            newChannels.addAll(channels)
            newChannels.removeIf { it.compareTo(newChannel) == 0 }
            newChannels.add(newChannel)
            return newChannels
        }

        private fun appendChannels(addedChannel: ChannelPresentationModel): TreeSet<ChannelPresentationModel> {
            val newChannels = TreeSet<ChannelPresentationModel>()
            newChannels.addAll(channels)
            newChannels.add(addedChannel)
            return newChannels
        }
    }
}