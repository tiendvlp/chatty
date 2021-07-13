package com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view

import android.view.ViewGroup
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView
import com.devlogs.chatty.screen.common.presentationmodel.UserPresentationModel
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.controller.ChannelRcvAdapter
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.ChannelMvcView.Listener
import java.util.*


interface ChannelMvcView: ObservableMvcView <Listener>{
    enum class ErrorType {
        Network,
        LoadUserError
    }

    interface Listener {
        fun onLoadMoreChannel()
        fun onRefreshChannel ()
        fun onUserSelectedChannel (selectedChannel: ChannelPresentationModel)
    }

    fun saveState()
    fun showLoading ()
    fun showError (errorType: ErrorType)
    fun display (channels: TreeSet<ChannelPresentationModel>)
    fun showMoreChannel (channels: TreeSet<ChannelPresentationModel>)
    fun showLoadMoreError (errorType: ErrorType)
    fun showTopError (errorType: ErrorType)
    fun hideTopError ()
    fun showRefresh ()
    fun hideRefresh ()
    fun showLoadUserError ()
    fun hideError ()
    fun showUserInfo (user: UserPresentationModel)
    fun showNewChannel (newChannel : ChannelPresentationModel)
    fun showReloadedChannel (channels: TreeSet<ChannelPresentationModel>)
    fun updateChannel(channels: ChannelPresentationModel)
}

fun MvcViewFactory.getMainMvcView (container: ViewGroup?, channelRcvAdapter: ChannelRcvAdapter, screenStateManager: PresentationStateManager) : ChannelMvcView = ChannelMvcViewImp(uiToolkit, container, channelRcvAdapter,screenStateManager)