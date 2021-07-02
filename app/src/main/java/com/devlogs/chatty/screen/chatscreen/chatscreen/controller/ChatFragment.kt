package com.devlogs.chatty.screen.chatscreen.chatscreen.controller

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.common.application.ApplicationEventObservable
import com.devlogs.chatty.common.helper.normalLog
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
class ChatFragment : Fragment(), ChatMvcView.Listener, PresentationStateChangedListener, ServiceConnection {
    companion object {
        fun getInstance () : ChatFragment {
            return ChatFragment()
        }
    }

    @Inject
    protected lateinit var mvcViewFactory: MvcViewFactory
    @Inject
    protected lateinit var presentationStateManager: PresentationStateManager
    @Inject
    protected lateinit var applicationEventObservable: ApplicationEventObservable
    @Inject
    protected lateinit var loadChatController: LoadChatController
    private lateinit var mvcView: ChatMvcView

    val channelID = "60cf0b5193d24d143385f257"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        mvcView = mvcViewFactory.getChatMvcView(container)
        return mvcView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        mvcView.register(this)
        presentationStateManager.register(this, true)
    }

    override fun onStop() {
        mvcView.register(this)
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
            }

            is RestoreAction -> {
                mvcView.showChat(currentState.data)
            }

            is LoadMoreChatFailedAction -> {
            }

            is LoadMoreChatSuccessAction -> {
                mvcView.showMore(action.data)
            }

            is ReloadChatFailedAction -> {

            }

            is ReLoadChatSuccessAction -> {
            }

            is NewChatAction -> {
            }
        }
    }//

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    override fun onBtnSendClicked(message: String) {
    }

    override fun onLoadMore() {
        loadChatController.loadMoreChat(channelID, (presentationStateManager.currentState as ChatScreenState).latestTime)
    }
}