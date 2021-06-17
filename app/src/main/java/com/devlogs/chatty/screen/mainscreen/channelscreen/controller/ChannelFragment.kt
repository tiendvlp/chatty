package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.androidservice.bindSocketEventService
import com.devlogs.chatty.channel.GetUserChannelsOverPeriodOfTimeUseCaseSync
import com.devlogs.chatty.common.application.ApplicationEventObservable
import com.devlogs.chatty.common.application.ServerConnectionEvent
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.domain.entity.channel.ChannelEntity
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import com.devlogs.chatty.realtime.ChannelRealtime
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateChangedListener
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.to
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.ChannelMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.getMainMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction.*
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationState.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class ChannelFragment : Fragment(), ChannelMvcView.Listener, PresentationStateChangedListener,
    ServerConnectionEvent, ServiceConnection {

    companion object {
        fun getInstance(): ChannelFragment {
            normalLog("New instance")
            return ChannelFragment()
        }
    }

    @Inject
    lateinit var channelRcvAdapter: ChannelRcvAdapter

    @Inject
    lateinit var loginWithEmailUseCaseSync: LoginWithEmailUseCaseSync

    @Inject
    lateinit var getChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync

    @Inject
    lateinit var mvcViewFactory: MvcViewFactory

    @Inject
    lateinit var presentationStateManager: PresentationStateManager

    @Inject
    lateinit var loadChannelController: LoadChannelController

    @Inject
    lateinit var applicationEventObservable: ApplicationEventObservable
    @Inject
    lateinit var channelSocketController: ChannelSocketEventListener

    private lateinit var mvcView: ChannelMvcView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presentationStateManager.init(savedInstanceState, LoadingState);
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
        presentationStateManager.init(savedInstanceState, LoadingState)
        mvcView = mvcViewFactory.getMainMvcView(container, channelRcvAdapter)
        return mvcView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        normalLog("onStart")
        mvcView.register(this)
        applicationEventObservable.register(this)
        presentationStateManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        mvcView.unRegister(this)
        applicationEventObservable.unRegister(this)
        presentationStateManager.unRegister(this)
    }

    override fun onLoadMoreChannel() {
        loadChannelController.loadMoreChannels()
    }

    override fun onRefreshChannel() {
        loadChannelController.reloadChannels()
    }

    override fun onStateChanged(
        previousState: PresentationState,
        currentState: PresentationState,
        action: PresentationAction
    ) {
        when (currentState) {
            is DisplayState -> {
                displayStateProcess(currentState, previousState, action)
            }
            is LoadingState -> {
                channelSocketController.onStop()
                loadChannelController.getLoadedChannel()
                mvcView.showLoading()
            }
            is ErrorState -> {
                channelSocketController.onStop()
                mvcView.showError(ChannelMvcView.ErrorType.Network)
            }
            else -> throw Exception("Invalid state error: ChannelFragment")
        }
    }

    private fun displayStateProcess(
        displayStateInstance: DisplayState,
        previousState: PresentationState,
        action: PresentationAction
    ) {
        when (action) {
            is LoadChannelSuccessAction -> {
                channelSocketController.onStart()
                loadChannelController.getMyUser()
                mvcView.display(action.data)
            }
            is LoadUserFailedAction -> {
                mvcView.showLoadUserError()
            }
            is LoadUserSuccessAction -> {
                mvcView.showUserInfo(action.user)
            }

            is LoadMoreChannelFailedAction -> {
                mvcView.showLoadMoreError(ChannelMvcView.ErrorType.Network)
            }

            is LoadMoreChannelSuccessAction -> {
                mvcView.showMoreChannel(action.data)
            }

            is ReloadChannelFailedAction -> {
                normalLog("Reload channel failed")
                mvcView.showTopError(ChannelMvcView.ErrorType.Network)
            }

            is ReLoadChannelSuccessAction -> {
                normalLog("Reload channel success")
                mvcView.showReloadedChannel(action.data)
            }

            is NewChannelAction -> {
                mvcView.showNewChannel(action.data)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onServerDisconnected() {
        mvcView.showTopError(ChannelMvcView.ErrorType.Network)
    }

    override fun onServerConnected() {
        mvcView.hideTopError()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }
}