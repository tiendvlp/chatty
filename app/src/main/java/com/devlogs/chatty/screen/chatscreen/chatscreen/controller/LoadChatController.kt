package com.devlogs.chatty.screen.chatscreen.chatscreen.controller

import com.devlogs.chatty.chat.LoadMoreChatUseCaseSync
import com.devlogs.chatty.chat.ReloadChatUseCaseSync
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.to
import com.devlogs.chatty.screen.chatscreen.chatscreen.state.ChatScreenAction.*
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class LoadChatController @Inject constructor(
    private val presentationStateManager: PresentationStateManager,
    private val loadMoreChatUseCaseSync: LoadMoreChatUseCaseSync,
    private val reloadChatUseCaseSync: ReloadChatUseCaseSync,
    ) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    fun loadMoreChat (channelId: String, since: Long) {
        coroutineScope.launch {
            normalLog("LoadMoreChatStared")
            val result = loadMoreChatUseCaseSync.execute(channelId, since)
            when (result) {
                is LoadMoreChatUseCaseSync.Result.Success -> {
                    normalLog("LoadMoreSuccess: ${result.data.size}")
                    val presentableModels = TreeSet<ChatPresentableModel>()
                    presentableModels.addAll(result.data.map { it.to() })
                    presentationStateManager.consumeAction(LoadMoreChatSuccessAction(presentableModels))
                }

                is LoadMoreChatUseCaseSync.Result.GeneralError -> {
                    presentationStateManager.consumeAction(LoadMoreChatFailedAction(result.message))
                }

                is LoadMoreChatUseCaseSync.Result.NetworkError -> {
                    presentationStateManager.consumeAction(LoadMoreChatFailedAction("Network error"))
                }
            }
        }
    }

    fun reloadChat (channelId: String, since: Long) {
        coroutineScope.launch {
            val result = reloadChatUseCaseSync.execute(channelId)

            when (result) {
                is ReloadChatUseCaseSync.Result.Success -> {
                    normalLog("ReloadMessageSuccess: ${result.data.size}")
                    val presentableModels = TreeSet<ChatPresentableModel>()
                    presentableModels.addAll(result.data.map { it.to() })
                    presentationStateManager.consumeAction(ReLoadChatSuccessAction(presentableModels))
                }
                is ReloadChatUseCaseSync.Result.GeneralError -> {
                    presentationStateManager.consumeAction(ReloadChatFailedAction)
                }
                is ReloadChatUseCaseSync.Result.NetworkError -> {
                    presentationStateManager.consumeAction(ReloadChatFailedAction)
                }
            }
        }
    }
}