package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import com.devlogs.chatty.channel.GetUserChannelsOverPeriodOfTimeUseCaseSync
import com.devlogs.chatty.common.helper.toBitmap
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction.LoadChannelSuccessAction
import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

class LoadChannelController {
    private val mPresentationStateManager: PresentationStateManager
    private val mGetChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    constructor(
        getChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync,
        presentationStateManager: PresentationStateManager
    ) {
        mGetChannelOverPeriodOfTimeUseCaseSync = getChannelOverPeriodOfTimeUseCaseSync
        mPresentationStateManager = presentationStateManager
    }

    fun getChannels(since: Long) {

        var j: Job =
            scope.launch(NonCancellable + EmptyCoroutineContext + Dispatchers.Main.immediate) {
                val result = mGetChannelOverPeriodOfTimeUseCaseSync.execute(since, 10)
                if (result is GetUserChannelsOverPeriodOfTimeUseCaseSync.Result.Success) {
                    mPresentationStateManager.consumeAction(LoadChannelSuccessAction(result.channels.map { channelEnitity ->
                        ChannelPresentationModel(
                            avatar = channelEnitity.members.map { it.avatar.toBitmap() },
                            title = channelEnitity.title,
                            sender = channelEnitity.status.senderEmail,
                            message = channelEnitity.status.content
                        )
                    }))
                }
                if (result is GetUserChannelsOverPeriodOfTimeUseCaseSync.Result.NetworkError) {
                    mPresentationStateManager.consumeAction(
                        ChannelScreenPresentationAction.LoadChannelFailedAction(
                            "Network Error"
                        )
                    )
                }
                if (result is GetUserChannelsOverPeriodOfTimeUseCaseSync.Result.GeneralError) {
                    mPresentationStateManager.consumeAction(
                        ChannelScreenPresentationAction.LoadChannelFailedAction(
                            "General Error"
                        )
                    )
                }
                if (result is GetUserChannelsOverPeriodOfTimeUseCaseSync.Result.InvalidRefreshToken) {
                    mPresentationStateManager.consumeAction(
                        ChannelScreenPresentationAction.LoadChannelFailedAction(
                            "UnAuthorized"
                        )
                    )
                }
            }
    }
}
