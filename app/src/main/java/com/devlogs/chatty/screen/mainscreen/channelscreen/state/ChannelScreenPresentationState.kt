package com.devlogs.chatty.screen.mainscreen.channelscreen.state

import com.devlogs.chatty.screen.common.presentationstate.InvalidActionException
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction.*

sealed class ChannelScreenPresentationState : PresentationState {
    override fun getTag(): String {
        return "ChannelScreenPresentationState"
    }

    object LoadingState : ChannelScreenPresentationState() {
        override fun consumeAction(previousState: PresentationState, action: PresentationAction): PresentationState {
            when (action) {
                is LoadChannelFailedAction -> {return ErrorState(action.errMessage)}
                is LoadChannelSuccessAction -> { return DisplayState (action.data) }
                is CancelAction -> {return LoadingState}
                is LoadUserSuccessAction -> {return this}
            }
            throw InvalidActionException("${getTag()}.LoadingState", action.toString())
        }
    }

    data class ErrorState(val errMessage: String) : ChannelScreenPresentationState() {
        override fun consumeAction(previousState: PresentationState, action: PresentationAction): PresentationState {
            when (action) {
                is LoadAction -> {return LoadingState}
                is LoadUserSuccessAction -> {return this}
            }
            throw InvalidActionException("${getTag()}.ErrorState", action.toString())
        }
    }

    object LoadMoreState : ChannelScreenPresentationState () {
        override fun consumeAction(previousState: PresentationState, action: PresentationAction): PresentationState {
            when (action) {
                is LoadChannelSuccessAction -> return DisplayState (action.data)
                is LoadMoreChannelFailedAction -> return previousState
                is CancelAction -> return previousState
                is LoadUserSuccessAction -> {return this}
            }
            throw InvalidActionException("${getTag()}.LoadMoreState", action.toString())
        }

    }

    data class DisplayState (val channels : List<ChannelPresentationModel>) : ChannelScreenPresentationState() {
        override fun consumeAction(previousState: PresentationState, action: PresentationAction): PresentationState {
            when (action) {
                is LoadMoreChannelAction -> return LoadMoreState
                is LoadUserSuccessAction -> return this
            }
            throw InvalidActionException("${getTag()}.DisplayState", action.toString())
        }
    }
}