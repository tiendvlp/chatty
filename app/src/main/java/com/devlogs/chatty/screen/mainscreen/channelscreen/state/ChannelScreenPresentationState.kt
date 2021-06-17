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
            throw InvalidActionException("${getTag()}.LoadingState", action.toString())
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
            throw InvalidActionException("${getTag()}.ErrorState", action.toString())
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
            throw InvalidActionException("${getTag()}.DisplayState", action.toString())
        }

        private fun appendChannels(addedChannels: TreeSet<ChannelPresentationModel>): TreeSet<ChannelPresentationModel> {
            val newChannels = TreeSet<ChannelPresentationModel>()
            newChannels.addAll(channels)
            newChannels.addAll(addedChannels)
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