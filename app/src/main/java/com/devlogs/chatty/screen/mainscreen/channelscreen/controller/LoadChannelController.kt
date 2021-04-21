package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import com.devlogs.chatty.channel.GetBeforeUserChannelsWithCountUseCaseSync
import com.devlogs.chatty.channel.GetUserChannelsOverPeriodOfTimeUseCaseSync
import com.devlogs.chatty.channel.LoadMoreUseCaseSync
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.common.helper.toBitmap
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction.LoadChannelSuccessAction
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

class LoadChannelController {
    private val mPresentationStateManager: PresentationStateManager
    private val mGetChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private val getBeforeChannelWithCountUseCase: GetBeforeUserChannelsWithCountUseCaseSync
    private val loadMoreChannel: LoadMoreUseCaseSync

    @Inject
    constructor(
        getChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync,
        presentationStateManager: PresentationStateManager,
        getBeforeChannelWithCountUseCase: GetBeforeUserChannelsWithCountUseCaseSync,
        loadMoreChannel: LoadMoreUseCaseSync
    ) {
        mGetChannelOverPeriodOfTimeUseCaseSync = getChannelOverPeriodOfTimeUseCaseSync
        mPresentationStateManager = presentationStateManager
        this.getBeforeChannelWithCountUseCase = getBeforeChannelWithCountUseCase
        this.loadMoreChannel = loadMoreChannel
    }

    fun getChannels(since: Long) {
        scope.launch(Dispatchers.Main.immediate) {
            val result = loadMoreChannel.execute(since);
            if (result is LoadMoreUseCaseSync.Result.Success) {
                normalLog("LoadChannelSuccess with count: ${result.channels.size}")
                mPresentationStateManager.consumeAction(LoadChannelSuccessAction(result.channels.map { channelEntity ->
                    ChannelPresentationModel(
                        avatar = channelEntity.members.map {
                            async(BackgroundDispatcher) {
                                it.avatar.toBitmap()
                            }.await()
                        },
                        title = channelEntity.title,
                        sender = channelEntity.status.senderEmail,
                        message = channelEntity.status.content
                    )
                }))
                return@launch
            }
            if (result is LoadMoreUseCaseSync.Result.NetworkError) {
                normalLog("Load channel failed due to network error")
                mPresentationStateManager.consumeAction(
                    ChannelScreenPresentationAction.LoadChannelFailedAction(
                        "Network Error"
                    )
                )
                return@launch
            }
            if (result is LoadMoreUseCaseSync.Result.GeneralError) {
                normalLog("Load channel failed due to general error")
                mPresentationStateManager.consumeAction(
                    ChannelScreenPresentationAction.LoadChannelFailedAction(
                        "General Error"
                    )
                )
                return@launch
            }
            if (result is LoadMoreUseCaseSync.Result.UnAuthorized) {
                normalLog("Load channel failed invalid refreshtoken")
                mPresentationStateManager.consumeAction(
                    ChannelScreenPresentationAction.LoadChannelFailedAction(
                        "UnAuthorized"
                    )
                )
                return@launch
            }

        }
    }
}
