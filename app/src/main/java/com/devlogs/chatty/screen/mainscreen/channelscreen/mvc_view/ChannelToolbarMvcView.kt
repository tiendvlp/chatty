package com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import de.hdodenhof.circleimageview.CircleImageView

class ChannelToolbarMvcView : BaseMvcView <ChannelToolbarMvcView.Listener> {
    interface Listener {
        fun onBtnAccountClicked ()
        fun onBtnNewChatClicked ()
    }

    private lateinit var btnNewChat: ImageButton
    private lateinit var imgAvatar: CircleImageView

    constructor(inflater: LayoutInflater, container: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.layout_maintoolbar, container, false))
        addControls()
        addEvents()
    }

    private fun addControls() {
        btnNewChat = findViewById(R.id.btnNewChat)
        imgAvatar = findViewById(R.id.imgAvatar)
    }

    private fun addEvents() {
        btnNewChat.setOnClickListener {
            getListener().forEach {
                it.onBtnNewChatClicked()
            }
        }

        imgAvatar.setOnClickListener {
            getListener().forEach {
                it.onBtnAccountClicked()
            }
        }
    }

}