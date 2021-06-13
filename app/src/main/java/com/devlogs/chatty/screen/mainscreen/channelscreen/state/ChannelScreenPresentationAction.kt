package com.devlogs.chatty.screen.mainscreen.channelscreen.state

import com.devlogs.chatty.screen.common.presentationmodel.UserPresentationModel
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import java.util.*

sealed class ChannelScreenPresentationAction : PresentationAction {
    object LoadAction : ChannelScreenPresentationAction()
    object CancelAction: ChannelScreenPresentationAction()
    data class LoadChannelFailedAction(val errMessage: String): ChannelScreenPresentationAction()
    data class LoadChannelSuccessAction (val data: TreeSet<ChannelPresentationModel>): ChannelScreenPresentationAction()

    object LoadMoreChannelAction: ChannelScreenPresentationAction()
    data class LoadMoreChannelFailedAction(val errMessage: String): ChannelScreenPresentationAction()
    data class LoadMoreChannelSuccessAction (val data: TreeSet<ChannelPresentationModel>): ChannelScreenPresentationAction()

    data class LoadUserSuccessAction(val user: UserPresentationModel) : ChannelScreenPresentationAction()
    object LoadUserFailedAction : ChannelScreenPresentationAction()

    data class ReLoadChannelSuccessAction (val data: TreeSet<ChannelPresentationModel>): ChannelScreenPresentationAction()
    object ReloadChannelFailedAction : ChannelScreenPresentationAction()
}