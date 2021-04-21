package com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view

import android.view.ViewGroup
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.ChannelMvcView.Listener

interface ChannelMvcView: ObservableMvcView <Listener>{
    interface Listener {
        fun loadMore()
    }

    fun loading ()
    fun loadingFailed ()
    fun channelLoaded (channels: List<ChannelPresentationModel>)
}

fun MvcViewFactory.getMainMvcView (container: ViewGroup?) : ChannelMvcView = ChannelMvcViewImp(getLayoutInflater(), container)