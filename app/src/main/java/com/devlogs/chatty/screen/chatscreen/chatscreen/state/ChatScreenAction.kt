package com.devlogs.chatty.screen.chatscreen.chatscreen.state

import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import java.util.*

sealed class ChatScreenAction : PresentationAction {
    object LoadAction : ChatScreenAction()
    data class LoadChatFailedAction(val errMessage: String): ChatScreenAction()
    data class LoadChatSuccessAction (val data: TreeSet<ChatPresentableModel>): ChatScreenAction()

    object LoadMoreChatAction: ChatScreenAction()
    data class LoadMoreChatFailedAction(val errChat: String): ChatScreenAction()
    data class LoadMoreChatSuccessAction (val data: TreeSet<ChatPresentableModel>): ChatScreenAction()

    data class ReLoadChatSuccessAction (val data: TreeSet<ChatPresentableModel>): ChatScreenAction()
    object ReloadChatFailedAction : ChatScreenAction()
    
    data class NewChatAction (val data: ChatPresentableModel) : ChatScreenAction()
}