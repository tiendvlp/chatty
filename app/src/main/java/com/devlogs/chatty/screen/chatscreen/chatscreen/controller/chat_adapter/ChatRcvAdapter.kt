package com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.devlogs.chatty.R

private const val YOUR_CHAT = 1
private const val FRIEND_CHAT = 2

class ChatRcvAdapter : Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == YOUR_CHAT) {
            return YourChatViewHolder(inflater.inflate(R.layout.item_yourchat, parent,false))
        }
        return FriendChatViewHolder(inflater.inflate(R.layout.item_friendchat, parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 20

    override fun getItemViewType(position: Int): Int {
        if (position % 2 == 0) return YOUR_CHAT
        return FRIEND_CHAT
    }
}