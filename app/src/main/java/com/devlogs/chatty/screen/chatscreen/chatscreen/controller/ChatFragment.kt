package com.devlogs.chatty.screen.chatscreen.chatscreen.controller

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.androidservice.sendmessage.SendMessageService
import com.devlogs.chatty.common.application.ApplicationEventObservable
import com.devlogs.chatty.common.application.MessageListener
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.domain.entity.message.MessageEntity
import com.devlogs.chatty.screen.chatscreen.ChatActivity
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.to
import com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view.ChatMvcView
import com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view.getChatMvcView
import com.devlogs.chatty.screen.chatscreen.chatscreen.state.ChatScreenAction.*
import com.devlogs.chatty.screen.chatscreen.chatscreen.state.ChatScreenState
import com.devlogs.chatty.screen.chatscreen.chatscreen.state.ChatScreenState.*
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.presentationstate.CommonPresentationAction.RestoreAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateChangedListener
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(), ChatMvcView.Listener, PresentationStateChangedListener, MessageListener, ServiceConnection {
    companion object {
        private val CHANNEL_ID_PARAM = "CHANNEL_ID"
        fun getInstance (channelId: String) : ChatFragment {
            val fragment =  ChatFragment()
            val arguments = Bundle()
            arguments.putString(CHANNEL_ID_PARAM, channelId)
            fragment.arguments = arguments
            return fragment
        }
    }

    @Inject
    protected lateinit var mvcViewFactory: MvcViewFactory
    @Inject
    protected lateinit var chatEventListener: ChatEventListener
    @Inject
    protected lateinit var presentationStateManager: PresentationStateManager
    @Inject
    protected lateinit var applicationEventObservable: ApplicationEventObservable
    @Inject
    protected lateinit var loadChatController: LoadChatController

    private lateinit var mvcView : ChatMvcView
    private var sendMessageService: SendMessageService? = null

    var channelID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.channelID = arguments?.get(CHANNEL_ID_PARAM) as String
        presentationStateManager.init(savedInstanceState, LoadingState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        presentationStateManager.onSavedInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        channelID = requireActivity().intent.extras!!.get(ChatActivity.CHANNEL_ID_PARAM) as String
        normalLog("Selected channel: $channelID")
        mvcView = mvcViewFactory.getChatMvcView(container)
        return mvcView.getRootView()
    }

    override fun onDestroy() {
        super.onDestroy()
        normalLog("Chat fragment get destroyed")
    }

    override fun onStart() {
        super.onStart()
        mvcView.register(this)
        chatEventListener.onStart()
        SendMessageService.bind(requireContext(), this)
        presentationStateManager.register(this, true)
    }

    override fun onStop() {
        mvcView.register(this)
        chatEventListener.onStop()
        presentationStateManager.register(this)
        super.onStop()
    }

    override fun onStateChanged(
        previousState: PresentationState?,
        currentState: PresentationState,
        action: PresentationAction
    ) {
        when (currentState) {
            is DisplayState -> {
                processDisplayState(previousState, currentState, action)
            }
            is LoadingState -> {
                loadChatController.loadMoreChat(channelID, (presentationStateManager.currentState as ChatScreenState).latestTime)
            }
            is ErrorState -> {

            }
            else -> throw Exception("Invalid state error: ChatFragment")
        }
    }

    private fun processDisplayState ( previousState: PresentationState?,
                                      currentState: DisplayState,
                                      action: PresentationAction) {
        normalLog("Load Chat Success Action: $action")
        when (action) {
            is LoadChatSuccessAction -> {
                mvcView.showChat(currentState.data)
                loadChatController.reloadChat(channelID)
            }

            is RestoreAction -> {
                mvcView.showChat(currentState.data)
                loadChatController.reloadChat(channelID)
            }

            is LoadMoreChatFailedAction -> {
            }

            is LoadMoreChatSuccessAction -> {
                mvcView.showMore(action.data)
            }

            is ReloadChatFailedAction -> {

            }

            is ReLoadChatSuccessAction -> {
                mvcView.newChat(action.data)
            }

            is NewChatAction -> {
                normalLog("New Chat action")
                val data = TreeSet<ChatPresentableModel> ()
                data.add(action.data)
                mvcView.newChat(data)
            }
        }
    }


    override fun onBtnSendClicked(message: String) {
        sendMessageService?.sendTextMessage(message, channelID, null)
    }

    override fun onLoadMore() {
        loadChatController.loadMoreChat(channelID, (presentationStateManager.currentState as ChatScreenState).latestTime)
    }

    override fun onNewMessage(newMessage: MessageEntity) {
        if (newMessage.channelId.equals(channelID)) {
            normalLog("New chat: " + newMessage.content)
            presentationStateManager.consumeAction(NewChatAction(newMessage.to()))
        }
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        sendMessageService = (binder as SendMessageService.LocalBinder).service
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        sendMessageService = null
    }
}