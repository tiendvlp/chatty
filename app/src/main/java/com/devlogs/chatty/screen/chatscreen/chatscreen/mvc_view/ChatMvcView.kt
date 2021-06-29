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

    }

    fun showMore(data: TreeSet<ChatPresentableModel>)
}

fun MvcViewFactory.getChatMvcView (container: ViewGroup?) : ChatMvcView = ChatMvcViewImp(uiToolkit, container)