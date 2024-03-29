package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.view.ViewGroup
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit
import java.util.*

interface ChatMvcView : ObservableMvcView<ChatMvcView.Listener> {
    interface Listener {
         fun onBtnSendClicked(message: String)
        fun onLoadMore()

    }

    fun showMore(data: TreeSet<ChatPresentableModel>)
    fun showChat (data: TreeSet<ChatPresentableModel>)
    fun newChat(data: TreeSet<ChatPresentableModel>) {

    }

    fun updateMessage(message: ChatPresentableModel, identify: String?)
}

fun MvcViewFactory.getChatMvcView (container: ViewGroup?) : ChatMvcView = ChatMvcViewImp(uiToolkit, container)