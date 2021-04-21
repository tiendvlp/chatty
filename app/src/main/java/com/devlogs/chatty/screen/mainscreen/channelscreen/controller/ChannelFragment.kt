package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.channel.GetUserChannelsOverPeriodOfTimeUseCaseSync
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateChangedListener
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.ChannelMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.getMainMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationState
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationState.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChannelFragment : Fragment(), ChannelMvcView.Listener, PresentationStateChangedListener {

    companion object {
        fun getInstance () : ChannelFragment {
            return ChannelFragment()
        }
    }

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

    private lateinit var mChannelMvcView : ChannelMvcView

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
        CoroutineScope(Dispatchers.Main.immediate).launch {
        }
        mChannelMvcView = mvcViewFactory.getMainMvcView(container)
        return mChannelMvcView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        mChannelMvcView.register(this)
        presentationStateManager.register(this)
    }

    override fun onStop() {
        super.onStop()
        mChannelMvcView.unRegister(this)
        presentationStateManager.unRegister(this)
    }

    override fun loadMore() {

    }

    override fun onStateChanged(
        previousState: PresentationState,
        currentState: PresentationState,
        action: PresentationAction
    ) {
        when (currentState) {
            is DisplayState -> {
                mChannelMvcView.channelLoaded(currentState.channels)
            }
            is LoadingState -> {
                loadChannelController.getChannels(Date().time)
                mChannelMvcView.loading()
            }
            is ErrorState -> {
                mChannelMvcView.loadingFailed()
            }
            is LoadMoreState -> {
            }
            else -> throw Exception("Invalid state error: ChannelFragment")
        }
    }


}