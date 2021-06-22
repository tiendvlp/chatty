package com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.viewholder.TextChatViewHolder
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel

private const val TEXT_CHAT = 1
private const val VIDEO_CHAT = 2
private const val URL_CHAT = 3

class ChatRcvAdapter : Adapter<RecyclerView.ViewHolder> {

    private var source: ArrayList<ChatPresentableModel> = arrayListOf()
    private var sharedBox: ChatAdapterSharedBox

    constructor(sharedBox: ChatAdapterSharedBox) {
        this.sharedBox = sharedBox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TextChatViewHolder(inflater, parent, source, sharedBox)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TextChatViewHolder) {

            holder.bind(source[position], position, if (source[position].senderEmail.equals("tiendvlp@gmail.com")) TextChatViewHolder.Type.ORIGIN else TextChatViewHolder.Type.OPPOSITE )
        }
    }

    override fun getItemCount(): Int = source.size
    fun setSource(source: ArrayList<ChatPresentableModel>) {
        this.source.clear()
        this.source.addAll(source)
    }

}