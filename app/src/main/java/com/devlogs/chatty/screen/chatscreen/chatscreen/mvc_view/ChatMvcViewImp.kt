package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.ChatRcvAdapter
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit


class ChatMvcViewImp : ChatMvcView, BaseMvcView<ChatMvcView.Listener> {

    private val toolbarMvcView: ChatToolbarMvcView
    private val chatBoxMvcView: ChatBoxMvcView
    private val toolbar: Toolbar
    private val chatBoxContainer: FrameLayout
    private val lvChat: RecyclerView
    private val chatRcvAdapter: ChatRcvAdapter

    constructor(toolKit: UIToolkit, container: ViewGroup?) {
        setRootView(toolKit.layoutInflater.inflate(R.layout.layout_chat, container, false))
        toolbar = findViewById(R.id.toolbar)
        toolbarMvcView = ChatToolbarMvcView(toolKit, toolbar);
        chatBoxContainer = findViewById(R.id.chatBoxContainer)
        chatRcvAdapter = ChatRcvAdapter()
        toolbar.addView(toolbarMvcView.getRootView())
        chatBoxMvcView = ChatBoxMvcView(toolKit, chatBoxContainer)
        chatBoxContainer.addView(chatBoxMvcView.getRootView())
        lvChat = findViewById(R.id.lvChat)
        lvChat.layoutManager = LinearLayoutManager(getContext())
        lvChat.adapter = chatRcvAdapter

    }


}