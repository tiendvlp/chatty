package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.os.Build
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devlogs.chatty.R
import com.devlogs.chatty.chat.spawnMessage
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.ChatAdapterSharedBox
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.ChatRcvAdapter
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatType
import com.devlogs.chatty.screen.common.compat.ChattyCompat
import com.devlogs.chatty.screen.common.compat.KeyboardMovementCompatListener
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit

class ChatMvcViewImp : ChatMvcView, BaseMvcView<ChatMvcView.Listener>,
    KeyboardMovementCompatListener {

    private val toolbarMvcView: ChatToolbarMvcView
    private val chatBoxMvcView: ChatBoxMvcView
    private val toolbar: Toolbar
    private val chatBoxContainer: FrameLayout
    private val lvChat: RecyclerView
    private val chatRcvAdapter: ChatRcvAdapter
    private val layoutMain: ConstraintLayout

    @RequiresApi(Build.VERSION_CODES.R)
    constructor(toolKit: UIToolkit, container: ViewGroup?) {
        setRootView(toolKit.layoutInflater.inflate(R.layout.layout_chat, container, false))
        toolbar = findViewById(R.id.toolbar)
        layoutMain = findViewById(R.id.layoutMain)
        val compat = ChattyCompat(container!!, getRootView(), toolKit.window)
        compat.setupUiWindowInsets()
        compat.setupKeyboardAnimations()
        toolbarMvcView = ChatToolbarMvcView(toolKit, toolbar);
        chatBoxContainer = findViewById(R.id.chatBoxContainer)
        chatRcvAdapter = ChatRcvAdapter(ChatAdapterSharedBox(getContext()))
        toolbar.addView(toolbarMvcView.getRootView())
        chatBoxMvcView = ChatBoxMvcView(toolKit, chatBoxContainer)
        chatBoxContainer.addView(chatBoxMvcView.getRootView())
        lvChat = findViewById(R.id.lvChat)
        val manager = LinearLayoutManager(getContext())
        manager.stackFromEnd = true
        lvChat.layoutManager = manager
        chatRcvAdapter.setSource(spawnMessage)
        lvChat.adapter = chatRcvAdapter
        normalLog("Is Root same with container: ${getRootView() == layoutMain}")
        compat.setKeyboardMovementCompatListener(this)
    }


    private fun setUpKeyboardAnimating (window: Window) {
    }

    override fun onStart() {
        chatBoxMvcView.onStart()
    }

    override fun onFinished() {
        chatBoxMvcView.onFinished()
    }

    override fun onProgress(delta: Int, distance: Int, maxDistance: Int) {
        chatBoxMvcView.onProgress(delta, distance, maxDistance)
    }

}