package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.view.ViewGroup
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit

interface ChatMvcView : ObservableMvcView<ChatMvcView.Listener> {
    interface Listener {

    }

}

fun MvcViewFactory.getChatMvcView (container: ViewGroup?) : ChatMvcView = ChatMvcViewImp(uiToolkit, container)