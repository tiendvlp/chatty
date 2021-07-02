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
import java.util.*

class ChatMvcViewImp : ChatMvcView, BaseMvcView<ChatMvcView.Listener>,
    KeyboardMovementCompatListener, ChatBoxMvcView.Listener {

    private val toolbarMvcView: ChatToolbarMvcView
    private val chatBoxMvcView: ChatBoxMvcView
    private val toolbar: Toolbar
    private val chatBoxContainer: FrameLayout
    private val lvChat: RecyclerView
    private val chatRcvAdapter: ChatRcvAdapter
    private val layoutMain: ConstraintLayout

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
        normalLog("Is Root same with container: ${getRootView() == layoutMain}")
        compat.setKeyboardMovementCompatListener(this)
        chatBoxMvcView.register(this)
        addEvents()
        chatRcvAdapter.setRecyclerView(lvChat)
    }


    private fun addEvents () {
        chatRcvAdapter.onLoadMore = {
            normalLog("Load moreeee")
            getListener().forEach { listener ->
                listener.onLoadMore()
            }
        }
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

    override fun onBtnSendClicked(message: String) {
        getListener().forEach {
            it.onBtnSendClicked(message)
        }
    }

    override fun showMore(data: TreeSet<ChatPresentableModel>) {
        chatRcvAdapter.isLoading = false
        if (data.isEmpty()) {
            chatRcvAdapter.isLoadMoreEnable = false
        } else {
            chatRcvAdapter.isLoadMoreEnable = true
            chatRcvAdapter.add(data)
        }
//        chatRcvAdapter.notifyItemRangeInserted(startIndex, data.size - 1)
    }

    override fun showChat(data: TreeSet<ChatPresentableModel>) {
        chatRcvAdapter.clear()
        lvChat.adapter = chatRcvAdapter
        chatRcvAdapter.add(data)
    }

}