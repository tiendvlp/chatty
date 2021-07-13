package com.devlogs.chatty.screen.chatscreen.chatscreen.state

import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.chatscreen.chatscreen.state.ChatScreenAction.*
import com.devlogs.chatty.screen.common.presentationstate.*
import com.devlogs.chatty.screen.common.presentationstate.CommonPresentationAction.InitAction
import java.util.*

sealed class ChatScreenState : PresentationState {
    override val allowSave: Boolean = false

    override fun getTag(): String {
        return "ChatScreenState" + javaClass.simpleName
    }

    var latestTime : Long = -10; get() {
        if (field < 0) return System.currentTimeMillis()
        return field
    }


    object LoadingState : ChatScreenState() {
        private lateinit var loadedChat: TreeSet<ChatPresentableModel>

        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): CauseAndEffect {
            when (action) {
                is InitAction -> {
                    loadedChat = TreeSet()
                    return CauseAndEffect(action, LoadingState)
                }
                is LoadChatFailedAction -> {
                    return CauseAndEffect(action, ErrorState(action.errMessage))
                }
                is LoadMoreChatSuccessAction -> {
                    loadedChat.addAll(action.data)
                    return checkState() ?: CauseAndEffect(action, LoadingState)
                }
                is LoadChatSuccessAction -> {
                    normalLog("LoadChat Success Action")
                    return CauseAndEffect(action, DisplayState(action.data))
                }
            }
            return super.consumeAction(previousState, action)
        }

        private fun checkState () : CauseAndEffect? {
            if (loadedChat.isNotEmpty()) {
                normalLog("Is not empty")
                return consumeAction(LoadingState,LoadChatSuccessAction(loadedChat))
            }
            return null
        }
    }

    data class ErrorState(val errMessage: String) : ChatScreenState() {
        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): CauseAndEffect {
            when (action) {
                is LoadAction -> {
                    return CauseAndEffect(action, LoadingState)
                }
            }
            return super.consumeAction(previousState, action)
        }
    }


    data class DisplayState constructor(val data: TreeSet<ChatPresentableModel>,val currentScrollPosition : Int = 0) :
        ChatScreenState() {

        init {
            if (data.isNotEmpty()) {
                updateLastestTime()
            }
            normalLog(Date(latestTime).toGMTString())
        }

        private fun updateLastestTime () {
            latestTime = data.first().createdDate
        }

        override fun consumeAction(
            previousState: PresentationState,
            action: PresentationAction
        ): CauseAndEffect {

            when (action) {
                is NewChatAction -> return CauseAndEffect(action, copy(data = appendChats(action.data)))
                is LoadMoreChatAction -> return CauseAndEffect(action, copy())
                is LoadMoreChatSuccessAction -> {
                    return CauseAndEffect(action, copy(data = appendChats(action.data)))
                }
                is LoadMoreChatFailedAction -> {return CauseAndEffect(action, copy())}

                is ReLoadChatSuccessAction -> {
                    return CauseAndEffect(action, copy(data = appendChats(action.data)))
                }
                is ReloadChatFailedAction -> {
                    return CauseAndEffect(action, copy())
                }
            }
            updateLastestTime()
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