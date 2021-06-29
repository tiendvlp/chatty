package com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.helper.convertDpToPx
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.viewholder.TextChatViewHolder
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import java.util.*

private const val TEXT_CHAT = 1
private const val VIDEO_CHAT = 2
private const val URL_CHAT = 3
private const val BALANCE_SIZE = 4


class ChatRcvAdapter : Adapter<RecyclerView.ViewHolder> {

    private var source: TreeSet<ChatPresentableModel> = TreeSet()
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
                source.elementAt(position),
                position,
                if (source.elementAt(position).senderEmail.equals(SharedMemory.email)) TextChatViewHolder.Type.ORIGIN else TextChatViewHolder.Type.OPPOSITE
            )
        }
    }

    override fun getItemCount(): Int = source.size + 1 // plus the balance_size item

    fun add (data: TreeSet<ChatPresentableModel>) {
        if (data.isEmpty()) return
        normalLog("Size reload: ${data.size}")
        source.addAll(data)
        notifyItemRangeChanged(0, data.size + 1)
    }

    fun setSource(source: TreeSet<ChatPresentableModel>) {
        this.source.clear()
        this.source.addAll(source)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            return BALANCE_SIZE
        }
        return TEXT_CHAT
    }

}