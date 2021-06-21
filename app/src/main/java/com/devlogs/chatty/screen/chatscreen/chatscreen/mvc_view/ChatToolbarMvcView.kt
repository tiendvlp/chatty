package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit

class ChatToolbarMvcView : BaseMvcView <ChatToolbarMvcView.Listener> {
    interface Listener {

    }

    constructor(toolKit: UIToolkit, container: ViewGroup) {
        setRootView(toolKit.layoutInflater.inflate(R.layout.layout_chat_toolbar, container, false))
    }
}