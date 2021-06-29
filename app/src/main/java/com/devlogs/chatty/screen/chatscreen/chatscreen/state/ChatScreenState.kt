package com.devlogs.chatty.screen.chatscreen.chatscreen.state

import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.chatscreen.chatscreen.state.ChatScreenAction.*
import com.devlogs.chatty.screen.common.presentationmodel.UserPresentationModel
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState
import java.util.*

sealed class ChatScreenState : PresentationState {
    override val allowSave: Boolean = false
    override fun getTag(): String {
        return "ChatScreenState" + javaClass.simpleName
    }

    var latestTime : Long = Date().time


    object LoadingState : ChatScreenState() {
        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): PresentationState {
            when (action) {
                is LoadChatFailedAction -> {
                    return ErrorState(action.errMessage)
                }
                is LoadChatSuccessAction -> {
                    return DisplayState(action.data)
                }
            }
            return super.consumeAction(previousState, action)
        }
    }

    data class ErrorState(val errMessage: String) : ChatScreenState() {
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


    data class DisplayState constructor( val data: TreeSet<ChatPresentableModel>) :
        ChatScreenState() {

        init {
            if (data.isNotEmpty()) {
                latestTime = data.first().createdDate
            }
            normalLog(Date(latestTime).toGMTString())
        }

        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): PresentationState {

            when (action) {
                is NewChatAction -> return copy(data = appendChats(action.data))
                is LoadMoreChatAction -> return copy()
                is LoadMoreChatSuccessAction -> {
                    return copy(data = appendChats(action.data))
                }
                is LoadMoreChatFailedAction -> {return copy()}

                is ReLoadChatSuccessAction -> {
                    return copy(data = appendChats(action.data))
                }
                is ReloadChatFailedAction -> {
                    return copy()
                }
                // refresh updateTime

            }
            return super.consumeAction(previousState, action)
        }

        private fun appendChats(addedChats: TreeSet<ChatPresentableModel>): TreeSet<ChatPresentableModel> {
            val newChats = TreeSet<ChatPresentableModel>()
            newChats.addAll(data)
            newChats.addAll(addedChats)
            return newChats
        }

        private fun replaceChat (newChat: ChatPresentableModel): TreeSet<ChatPresentableModel> {
            val newChats = TreeSet<ChatPresentableModel>()
            newChats.addAll(data)
            newChats.removeIf { it.compareTo(newChat) == 0 }
            newChats.add(newChat)
            return newChats
        }

        private fun appendChats(addedChat: ChatPresentableModel): TreeSet<ChatPresentableModel> {
            val newChats = TreeSet<ChatPresentableModel>()
            newChats.addAll(data)
            newChats.add(addedChat)
            return newChats
        }
    }
}