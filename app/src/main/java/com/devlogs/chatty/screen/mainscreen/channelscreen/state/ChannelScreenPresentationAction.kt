package com.devlogs.chatty.screen.mainscreen.channelscreen.state

import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel

sealed class ChannelScreenPresentationAction : PresentationAction {
    object LoadAction : ChannelScreenPresentationAction()
    data class LoadChannelFailedAction(val errMessage: String): ChannelScreenPresentationAction()
    data class LoadMoreChannelFailedAction(val errMessage: String): ChannelScreenPresentationAction()
    data class LoadChannelSuccessAction (val data: List<ChannelPresentationModel>): ChannelScreenPresentationAction()
    object LoadMoreChannelAction: ChannelScreenPresentationAction()
    object LoadUserSuccessAction: ChannelScreenPresentationAction()
    object CancelAction: ChannelScreenPresentationAction()
    object LoadUserAvatarAction : ChannelScreenPresentationAction()
}