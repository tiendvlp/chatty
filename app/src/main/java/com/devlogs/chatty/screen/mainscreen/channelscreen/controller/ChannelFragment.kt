package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.channel.GetUserChannelsOverPeriodOfTimeUseCaseSync
import com.devlogs.chatty.common.application.ApplicationEventObservable
import com.devlogs.chatty.common.application.ServerConnectionEvent
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import com.devlogs.chatty.resource.GetUserAvatarUrlUseCaseSync
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.presentationstate.*
import com.devlogs.chatty.screen.common.presentationstate.CommonPresentationAction.RestoreAction
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.ChannelMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.getMainMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction.*
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationState.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class ChannelFragment : Fragment(), ChannelMvcView.Listener, PresentationStateChangedListener,
    ServerConnectionEvent, ServiceConnection {

    companion object {
        fun getInstance(): ChannelFragment {
            return ChannelFragment()
        }
    }

    @Inject
    protected lateinit var getAvatarUrl: GetUserAvatarUrlUseCaseSync
    @Inject
    protected lateinit var channelSocketController: ChannelSocketEventListener
    private lateinit var mvcView: ChannelMvcView
    @Inject
    protected lateinit var loadChannelController: LoadChannelController
    @Inject
    protected lateinit var channelRcvAdapter: ChannelRcvAdapter
    @Inject
    protected lateinit var loginWithEmailUseCaseSync: LoginWithEmailUseCaseSync
    @Inject
    protected lateinit var getChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync
    @Inject
    protected lateinit var mvcViewFactory: MvcViewFactory
    @Inject
    protected lateinit var presentationStateManager: PresentationStateManager
     @Inject
    protected lateinit var applicationEventObservable: ApplicationEventObservable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        normalLog("OnCreate")
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
        normalLog("OnCreateView with state: ${presentationStateManager.currentState}")
        mvcView = mvcViewFactory.getMainMvcView(container, ChannelRcvAdapter(getAvatarUrl))
        return mvcView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        normalLog("onStart")
        mvcView.register(this)
        applicationEventObservable.register(this)
        presentationStateManager.register(this)
        presentationStateManager.register(this, true)
    }

    override fun onStop() {
        super.onStop()
        normalLog("OnStop")
        mvcView.unRegister(this)
        applicationEventObservable.unRegister(this)
        presentationStateManager.unRegister(this)
    }

    override fun onLoadMoreChannel() {
        normalLog("Load more")
        loadChannelController.loadMoreChannels()
    }

    override fun onRefreshChannel() {
        loadChannelController.reloadChannels()
    }

    override fun onStateChanged(
        previousState: PresentationState?,
        currentState: PresentationState,
        action: PresentationAction
    ) {
        when (currentState) {
            is DisplayState -> {
                displayStateProcess(currentState, previousState!!, action)
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
                mvcView.display(displayStateInstance.channels)
            }
            is RestoreAction -> {
                channelSocketController.onStart()
                loadChannelController.getMyUser()
                mvcView.display(displayStateInstance.channels)
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

            is ChannelUpdatedAction -> {
                mvcView.updateChannel (action.data)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
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