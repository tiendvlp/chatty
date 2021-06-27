package com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.devlogs.chatty.common.helper.convertDpToPx
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.viewholder.TextChatViewHolder
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel

private const val TEXT_CHAT = 1
private const val VIDEO_CHAT = 2
private const val URL_CHAT = 3
private const val BALANCE_SIZE = 4


class ChatRcvAdapter : Adapter<RecyclerView.ViewHolder> {

    private var source: ArrayList<ChatPresentableModel> = arrayListOf()
    private var sharedBox: ChatAdapterSharedBox

    constructor(sharedBox: ChatAdapterSharedBox) {
        this.sharedBox = sharedBox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TEXT_CHAT) {
            return TextChatViewHolder(inflater, parent, source, sharedBox)
        }
        val balanceItemView = LinearLayout(parent.context)
        balanceItemView.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                convertDpToPx(parent.context, 120f).toInt()
            )
        return object : RecyclerView.ViewHolder(balanceItemView) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TextChatViewHolder) {
            holder.bind(
                source[position],
                position,
                if (source[position].senderEmail.equals("tiendvlp@gmail.com")) TextChatViewHolder.Type.ORIGIN else TextChatViewHolder.Type.OPPOSITE
            )
        }
    }

    override fun getItemCount(): Int = source.size + 1 // plus the balance_size item

    fun setSource(source: ArrayList<ChatPresentableModel>) {
        this.source.clear()
        this.source.addAll(source)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            return BALANCE_SIZE
        }
        return TEXT_CHAT
    }

}