package com.devlogs.chatty.screen.mainscreen.channelscreen.state

import com.devlogs.chatty.screen.common.presentationmodel.UserPresentationModel
import com.devlogs.chatty.screen.common.presentationstate.InvalidActionException
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState
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
        ): PresentationState {
            when (action) {
                is LoadChannelFailedAction -> {
                    return ErrorState(action.errMessage)
                }
                is LoadChannelSuccessAction -> {
                    return DisplayState(action.data)
                }
                is CancelAction -> {
                    return ErrorState("Canceled")
                }
            }
            return super.consumeAction(previousState, action)
        }
    }

    data class ErrorState(val errMessage: String) : ChannelScreenPresentationState() {
        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): PresentationState {
            when (action) {
                is LoadAction -> {
                    return LoadingState
                }
            }
            return super.consumeAction(previousState, action)
        }
    }


    data class DisplayState(val channels: TreeSet<ChannelPresentationModel>, val user: UserPresentationModel? = null) :
        ChannelScreenPresentationState() {
        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): PresentationState {
            when (action) {
                is NewChannelAction -> return copy(channels = appendChannels(action.data))
                is ChannelUpdatedAction -> return copy(channels = replaceChannel(action.data))
                is LoadMoreChannelAction -> return copy()
                is LoadMoreChannelSuccessAction -> {
                    return copy(channels = appendChannels(action.data))
                }
                is LoadMoreChannelFailedAction -> {return copy()}

                is ReLoadChannelSuccessAction -> {
                    return copy(channels = appendChannels(action.data))
                }
                is ReloadChannelFailedAction -> {
                    return copy()
                }

                is LoadUserSuccessAction -> return copy(user=action.user)
                is LoadUserFailedAction -> return copy()
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